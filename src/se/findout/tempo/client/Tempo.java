package se.findout.tempo.client;

import java.util.List;

import se.findout.tempo.client.ModelEditorView.EditorCommandListener;
import se.findout.tempo.client.VersionView.SelectionChangeListener;
import se.findout.tempo.client.VersionView.SelectionChangedEvent;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tempo implements EntryPoint, EditorCommandListener, SelectionChangeListener {

	private VersionModel model;
	private VersionView versionView;
	private ModelModel modelModel;
	private ModelRepositoryServiceAsync modelRepoService = null;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
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
		
		retrieveAllChanges();
	}

	@Override
	public void change(Command change) {
		Version selectedVersion = versionView.getSelectedVersion();
		Version addedVersion = model.addVersion(selectedVersion, change);
		storeChange(selectedVersion, change);
		versionView.selectVersion(addedVersion);
	}
	
	private void retrieveAllChanges() {
		if (modelRepoService == null) {
			modelRepoService = GWT.create(ModelRepositoryService.class);
		}
		
		AsyncCallback<List<ChangeInfo>> callback = new AsyncCallback<List<ChangeInfo>>() {
		
		@Override
		public void onSuccess(List<ChangeInfo> result) {
			System.out
					.println("Tempo.retrieveAllChanges().new AsyncCallback() {...}.onSuccess(" + result + ")");
			for (ChangeInfo changeInfo : result) {
				model.addVersion(model.getVersionById(changeInfo.versionId), changeInfo.change);
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
	
	modelRepoService.getAllChanges(callback);
	}

	private void storeChange(Version baseVersion, Command change) {
		if (modelRepoService == null) {
			modelRepoService = GWT.create(ModelRepositoryService.class);
		}
		
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				System.out
						.println("Tempo.storeChange(...).new AsyncCallback() {...}.onSuccess()");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				System.out
						.println("Tempo.storeChange(...).new AsyncCallback() {...}.onFailure()");
				GWT.log("failed", caught);
			}
		};
		
		modelRepoService.addCommand(baseVersion.getName(), change, callback);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		model.switchVersion(event.getPrevSelectedVersion(), event.getNewSelectedVersion(), new VersionModel.ChangeIterator() {
			
			@Override
			public void undo(Command change) {
				System.out
						.println("undo(" + change.getDescription() + ")");
				change.undo(modelModel);
			}
			
			@Override
			public void execute(Command change) {
				System.out
						.println("execute(" + change.getDescription() + ")");
				change.execute(modelModel);
			}
		});
	}
}
