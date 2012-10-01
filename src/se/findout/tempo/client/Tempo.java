package se.findout.tempo.client;

import se.findout.tempo.client.ModelEditorView.ModelChangeListener;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Tempo implements EntryPoint, ModelChangeListener {

	private VersionsModel model;
	private VersionsView versionsView;

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
				
		model = new VersionsModel();
		
		versionsView = new VersionsView(model);
		versionsView.selectVersion(model.getInitialVersion());
		splitPanel.addSouth(versionsView, 200);
		
		ModelEditorView modelEditor = new ModelEditorView();
		modelEditor.addModelChangelListener(this);
		splitPanel.add(modelEditor);
		
		RootPanel.get().add(splitPanel);
	}

	@Override
	public void change(Change change) {
		System.out.println("versionsView.getSelectedVersion()=" + versionsView.getSelectedVersion());
		Version addedVersion = model.addVersion(versionsView.getSelectedVersion(), change);
		versionsView.selectVersion(addedVersion);
	}
}
