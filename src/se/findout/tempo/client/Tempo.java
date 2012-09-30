package se.findout.tempo.client;

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
public class Tempo implements EntryPoint {

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
				
		VersionsModel model = new VersionsModel();
		Version v1 = model.getInitialVersion();
		Version v2 = model.addVersion(v1, new Change());
		Version v3 = model.addVersion(v2, new Change());
		Version v4 = model.addVersion(v3, new Change());
		Version v31 = model.addVersion(v3, new Change());
		Version v32 = model.addVersion(v31, new Change());
		Version v33 = model.addVersion(v32, new Change());
		Version v5 = model.addVersion(v4, new Change());

		
		splitPanel.addSouth(new VersionsView(model), 200);
		
		splitPanel.add(new ModelEditorView());
		
		RootPanel.get().add(splitPanel);
	}
}
