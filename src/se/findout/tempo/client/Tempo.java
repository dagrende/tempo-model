package se.findout.tempo.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.findout.tempo.client.ModelEditorView.EditorCommandListener;
import se.findout.tempo.client.VersionTreeView.SelectionChangeListener;
import se.findout.tempo.client.VersionTreeView.SelectionChangedEvent;
import se.findout.tempo.client.login.LoginInfo;
import se.findout.tempo.client.login.LoginService;
import se.findout.tempo.client.login.LoginServiceAsync;
import se.findout.tempo.client.model.ChangeInfo;
import se.findout.tempo.client.model.Command;
import se.findout.tempo.client.model.ModelModel;
import se.findout.tempo.client.model.ParticipantInfo;
import se.findout.tempo.client.model.ParticipantsModel;
import se.findout.tempo.client.model.Version;
import se.findout.tempo.client.model.VersionTreeModel;

import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The main program for the Tempo Model editor. Creates a layout with a model
 * editor view, a version tree view and a participants view. It then acts as a
 * controller, listening to model changes, editor commands and messages on the
 * channel from the server. Received events are then converted into changes to the
 * models and sent as RPC calls to the server.
 */
public class Tempo implements EntryPoint, EditorCommandListener, SelectionChangeListener {
	private final static Logger logger = Logger.getLogger(Tempo.class.getName());
	private VersionTreeModel versionTreeModel;
	private VersionTreeView versionTreeView;
	private ModelModel modelModel;
	private ModelRepositoryServiceAsync modelRepoService = null;

	private LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account to access the Tempo Model application.");
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	private String modelPath;
	private SerializationStreamFactory pushServiceStreamFactory;
	private ParticipantsModel participantsModel;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		modelPath = Window.Location.getParameter("doc");
		if (modelPath == null) {
			modelPath = "unnamed";
		}

		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
			}

			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				signOutLink.setHref(loginInfo.getLogoutUrl());
				if (loginInfo.isLoggedIn()) {
					logger.log(Level.FINE, "channelToken=" + loginInfo.getChannelToken());
					setupUI();
				} else {
					loadLogin();
				}
			}
		});
	}

	private void loadLogin() {
		// render a link to the login dialog
		signInLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get().add(loginPanel);
	}

	/**
	 * We are logged in - set up the user interface
	 */
	public void setupUI() {
		setupChannel();

		// create all models and views
		modelModel = new ModelModel();
		ModelEditorView modelEditor = new ModelEditorView(modelModel);
		modelEditor.addModelChangelListener(this);
		
		versionTreeModel = new VersionTreeModel();
		versionTreeView = new VersionTreeView(versionTreeModel);
		versionTreeView.selectVersion(versionTreeModel.getInitialVersion());
		versionTreeView.addSelectionChangeListener(this);

		participantsModel = new ParticipantsModel();
		participantsModel.setParticipants(loginInfo.getParticipants());
		ParticpantsView particpantsView = new ParticpantsView(participantsModel);
		
		// create all layout panels and add the views
		final SplitLayoutPanel mainPanel = new SplitLayoutPanel(8);
		final SplitLayoutPanel versionsAndParticipants = new SplitLayoutPanel(8);
		versionsAndParticipants.addEast(particpantsView, 200);
		versionsAndParticipants.add(versionTreeView);
		mainPanel.addSouth(versionsAndParticipants, 200);
		mainPanel.add(modelEditor);
		RootPanel.get().add(mainPanel);
		RootPanel.get().add(signOutLink);
		
		
		// make the main panel always fit the window
		mainPanel.setWidth("99%");
		mainPanel.setHeight((Window.getClientHeight() - 21) + "px");
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int height = event.getHeight() - 20;
				mainPanel.setHeight(height + "px");
			}
		});

		mainPanel.getElement().getStyle().setProperty("border", "3px solid #e7e7e7");
		RootPanel.get().getElement().getStyle().setProperty("margin", "0px");

		retrieveAllChanges(modelPath);
	}

	/**
	 * Create a channel for loginInfo.token and open a listener to it, processing all events from the channel.
	 */
	private void setupChannel() {
		pushServiceStreamFactory = (SerializationStreamFactory) GWT.create(PushService.class);

		ChannelFactory.createChannel(loginInfo.getChannelToken(), new ChannelCreatedCallback() {
			@Override
			public void onChannelCreated(Channel channel) {
				channel.open(new SocketListener() {
					@Override
					public void onOpen() {
						logger.log(Level.FINE,
								"Channel opened for token " + loginInfo.getChannelToken());
					}

					@Override
					public void onMessage(String message) {
						logger.log(Level.FINE, "Received: " + message);
						ChangeInfo changeInfo = null;
						try {
							Object receivedObject = pushServiceStreamFactory.createStreamReader(
									message).readObject();
							if (receivedObject instanceof ChangeInfo) {
								// received a change from another user editing this model
								changeInfo = (ChangeInfo) receivedObject;
								Version selectedVersion = versionTreeView.getSelectedVersion();
								Version baseVersion = versionTreeModel.getVersionById(changeInfo
										.getBaseVersion());
								boolean isOnHead = versionTreeModel.getHeads()
										.contains(selectedVersion);
								Version newVersion = versionTreeModel.addVersion(baseVersion,
										changeInfo.getChange());
								if (isOnHead
										&& newVersion.getBase().getId() == selectedVersion.getId()) {
									versionTreeView.selectVersion(newVersion);
								}
							} else if (receivedObject instanceof ParticipantInfo) {
								// received a new changed participants list
								ParticipantInfo participantInfo = (ParticipantInfo) receivedObject;
								logger.log(Level.FINE, "received participantInfo n="
										+ participantInfo.getParticipants().size());
								participantsModel.setParticipants(participantInfo.getParticipants());
							}
						} catch (SerializationException e) {
							throw new RuntimeException("Unable to deserialize " + message, e);
						}
					}

					@Override
					public void onError(SocketError error) {
						logger.log(Level.FINE, "Error: " + error.getDescription());
					}

					@Override
					public void onClose() {
						logger.log(Level.FINE, "Channel closed!");
					}
				});
			}
		});
	}

	/**
	 * An editor command has been performed by user, but not yet applied to the model.
	 */
	@Override
	public void change(Command change) {
		Version selectedVersion = versionTreeView.getSelectedVersion();
		Version newVersion = versionTreeModel.addVersion(selectedVersion, change);
		storeChange(modelPath, newVersion);
		versionTreeView.selectVersion(newVersion);
	}

	/**
	 * Call the server to get all changes from the datastore, put them in the local version tree model and apply them to the local model model.
	 * 
	 * @param modelPath
	 */
	private void retrieveAllChanges(String modelPath) {
		if (modelRepoService == null) {
			modelRepoService = GWT.create(ModelRepositoryService.class);
		}

		AsyncCallback<List<ChangeInfo>> callback = new AsyncCallback<List<ChangeInfo>>() {

			@Override
			public void onSuccess(List<ChangeInfo> result) {
				System.out
						.println("Tempo.retrieveAllChanges().new AsyncCallback() {...}.onSuccess("
								+ result + ")");
				for (ChangeInfo changeInfo : result) {
					versionTreeModel.addVersion(
							versionTreeModel.getVersionById(changeInfo.getBaseVersion()),
							changeInfo.getChange());
				}
				
				// select head version of trunk
				versionTreeView.selectVersion(versionTreeModel.getHeads().get(0));
			}

			@Override
			public void onFailure(Throwable caught) {
				System.out.println("Tempo.storeChange(...).new AsyncCallback() {...}.onFailure()");
				GWT.log("failed", caught);
			}
		};

		modelRepoService.getAllChanges(modelPath, callback);
	}

	/**
	 * Sends a change to the server in order to store it and inform other editors about it.
	 * @param modelPath
	 * @param addedVersion
	 */
	private void storeChange(String modelPath, final Version addedVersion) {
		if (modelRepoService == null) {
			modelRepoService = GWT.create(ModelRepositoryService.class);
		}

		AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {

			@Override
			public void onSuccess(Integer result) {
				logger.log(Level.FINE, "Tempo.storeChange.callback.onSuccess()");
				addedVersion.setId(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.FINE, "Tempo.storeChange.callback.onFailure()");
				GWT.log("failed", caught);
			}
		};

		modelRepoService.addCommand(loginInfo.getChannelId(), modelPath, addedVersion.getBase()
				.getId(), addedVersion.getChange(), callback);
	}

	/**
	 * User have selected another version in the version tree view.
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		/**
		 * Guide a visitor from the current version in the tree, to the new one, and let him undo and execute himself through the changes.
		 */
		versionTreeModel.switchVersion(event.getPrevSelectedVersion(), event.getNewSelectedVersion(),
				new VersionTreeModel.ChangeVisitor() {

					@Override
					public void undo(Command change) {
						logger.log(Level.FINE, "undo(" + change.getDescription() + ")");
						change.undo(modelModel);
					}

					@Override
					public void execute(Command change) {
						logger.log(Level.FINE, "execute(" + change.getDescription() + ")");
						change.execute(modelModel);
					}
				});
	}
}
