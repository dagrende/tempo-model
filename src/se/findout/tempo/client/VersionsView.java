package se.findout.tempo.client;

import org.vaadin.gwtgraphics.client.DrawingArea;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class VersionsView extends FlowPanel {

	public VersionsView(VersionsModel versionsModel) {
		DrawingArea drawingArea = new DrawingArea(2000, 1000);
		drawingArea.getElement().getStyle()
				.setProperty("border", "3px solid #e7e7e7");
		add(new ScrollPanel(drawingArea));
	}

}
