package se.findout.tempo.client;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.findout.tempo.client.ModelEditorView.EditorCommandListener;
import se.findout.tempo.client.VersionView.SelectionChangeListener;
import se.findout.tempo.client.VersionView.SelectionChangedEvent;
import se.findout.tempo.client.login.LoginInfo;
import se.findout.tempo.client.login.LoginService;
import se.findout.tempo.client.login.LoginServiceAsync;
import se.findout.tempo.client.model.ChangeInfo;
import se.findout.tempo.client.model.Command;
import se.findout.tempo.client.model.ModelModel;
import se.findout.tempo.client.model.Version;
import se.findout.tempo.client.model.VersionModel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The main program for the Tempo Model editor.
 * Places an 
 */
public class Tempo implements EntryPoint, EditorCommandListener,
		SelectionChangeListener {
    private final static Logger logger = Logger.getLogger(Tempo.class.getName());
	private VersionModel model;
	private VersionView versionView;
	private ModelModel modelModel;
	private ModelRepositoryServiceAsync modelRepoService = null;

	private LoginInfo loginInfo = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label(
			"Please sign in to your Google Account to access the Tempo Model application.");
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	private String docPath;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		docPath = Window.Location.getParameter("doc");
		if (docPath == null) {
			docPath = "unnamed";
		}
		
		// Check login status using login service.
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {
					public void onFailure(Throwable error) {
					}

					public void onSuccess(LoginInfo result) {
						loginInfo = result;
						signOutLink.setHref(loginInfo.getLogoutUrl());
						if (loginInfo.isLoggedIn()) {
							setupUI();
						} else {
							loadLogin();
						}
					}
				});
//		setupUI();
	}

	private void loadLogin() {
		// Assemble login panel.
		signInLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get().add(loginPanel);
	}

	public void setupUI() {
		RootPanel.get().getElement().getStyle().setProperty("margin", "0px");
		final SplitLayoutPanel splitPanel = new SplitLayoutPanel(8);
		splitPanel.setWidth("99%");
		splitPanel.setHeight((Window.getClientHeight() - 21) + "px");
		Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int height = event.getHeight() - 20;
				splitPanel.setHeight(height + "px");
			}
		});
		splitPanel.getElement().getStyle()
				.setProperty("border", "3px solid #e7e7e7");

		model = new VersionModel();

		versionView = new VersionView(model);
		versionView.selectVersion(model.getInitialVersion());
		versionView.addSelectionChangeListener(this);
		splitPanel.addSouth(versionView, 200);

		modelModel = new ModelModel();
		ModelEditorView modelEditor = new ModelEditorView(modelModel);
		modelEditor.addModelChangelListener(this);
		splitPanel.add(modelEditor);
		RootPanel.get().add(splitPanel);
		RootPanel.get().add(signOutLink);

		retrieveAllChanges(docPath);
	}

	@Override
	public void change(Command change) {
		Version selectedVersion = versionView.getSelectedVersion();
		Version addedVersion = model.addVersion(selectedVersion, change);
		storeChange(docPath, selectedVersion, change);
		versionView.selectVersion(addedVersion);
	}

	private void retrieveAllChanges(String docPath) {
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
					model.addVersion(
							model.getVersionById(changeInfo.getVersionId()),
							changeInfo.getChange());
				}
				versionView.selectVersion(model.getHeads().get(0));
			}

			@Override
			public void onFailure(Throwable caught) {
				System.out
						.println("Tempo.storeChange(...).new AsyncCallback() {...}.onFailure()");
				GWT.log("failed", caught);
			}
		};

		modelRepoService.getAllChanges(docPath, callback);
	}

	private void storeChange(String docPath, Version baseVersion, Command change) {
		if (modelRepoService == null) {
			modelRepoService = GWT.create(ModelRepositoryService.class);
		}

		AsyncCallback<String> callback = new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				logger.log(Level.FINE, "Tempo.storeChange.callback.onSuccess()");
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.FINE, "Tempo.storeChange.callback.onFailure()");
				GWT.log("failed", caught);
			}
		};

		modelRepoService.addCommand(docPath, baseVersion.getName(), change, callback);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		model.switchVersion(event.getPrevSelectedVersion(),
				event.getNewSelectedVersion(),
				new VersionModel.ChangeIterator() {

					@Override
					public void undo(Command change) {
						logger.log(Level.FINE, "undo(" + change.getDescription()
								+ ")");
						change.undo(modelModel);
					}

					@Override
					public void execute(Command change) {
						logger.log(Level.FINE, "execute(" + change.getDescription()
								+ ")");
						change.execute(modelModel);
					}
				});
	}
}
