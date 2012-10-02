package se.findout.tempo.client;

import se.findout.tempo.client.ModelEditorView.ModelChangeListener;
import se.findout.tempo.client.VersionView.SelectionChangeListener;
import se.findout.tempo.client.VersionView.SelectionChangedEvent;

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
public class Tempo implements EntryPoint, ModelChangeListener, SelectionChangeListener {

	private VersionModel model;
	private VersionView versionView;

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
		
		ModelEditorView modelEditor = new ModelEditorView();
		modelEditor.addModelChangelListener(this);
		splitPanel.add(modelEditor);
		
		RootPanel.get().add(splitPanel);
	}

	@Override
	public void change(Change change) {
		System.out.println("versionView.getSelectedVersion()=" + versionView.getSelectedVersion());
		Version addedVersion = model.addVersion(versionView.getSelectedVersion(), change);
		versionView.selectVersion(addedVersion);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		System.out.println("Tempo.selectionChanged(from=" + event.getPrevSelectedVersion() + " to=" + event.getNewSelectedVersion() + ")");
		model.switchVersion(event.getPrevSelectedVersion(), event.getNewSelectedVersion());
	}
}
