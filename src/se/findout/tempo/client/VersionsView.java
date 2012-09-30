package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.shape.Circle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class VersionsView extends FlowPanel {

	private VersionsModel model;

	public VersionsView(VersionsModel versionsModel) {
		this.model = versionsModel;
		DrawingArea drawingArea = new DrawingArea(2000, 1000);
		drawingArea.getElement().getStyle()
				.setProperty("border", "3px solid #e7e7e7");
		add(new ScrollPanel(drawingArea));
		
		
	}

//	private void drawBranches(List<Branch> branches, int xi) {
//		int r = 20;
//		int xScale = 10 + r;
//		int yScale = 10 + r;
//		for (Branch branch : branches) {
//			add(new Circle(xi * xScale, branch.yi * yScale, r));
//		}
//	}

}
