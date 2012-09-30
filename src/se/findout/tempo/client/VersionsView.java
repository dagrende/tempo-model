package se.findout.tempo.client;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Text;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class VersionsView extends FlowPanel {

	private VersionsModel model;
	private DrawingArea drawingArea;

	public VersionsView(VersionsModel versionsModel) {
		this.model = versionsModel;
		drawingArea = new DrawingArea(2000, 1000);
		drawingArea.getElement().getStyle()
				.setProperty("border", "3px solid #e7e7e7");
		add(new ScrollPanel(drawingArea));
		
		
		VersionTreeRenderer renderer = new VersionTreeRenderer(model, new VersionTreeRenderer.ShapeFactory() {
			
			@Override
			public void addVersion(Version version, int x, int y) {
				Circle circle = new Circle(40 + x, 40 + y, 20);
				drawingArea.add(circle);
				Text text = new Text(0, 0, version.getName());
				text.setFillColor("black");
				text.setFontSize(14);
				text.setX(40 + x - text.getTextWidth() / 2);
				text.setY(40 + y + (int)(text.getTextHeight() / 2.7));
				drawingArea.add(text);
			}
			
			@Override
			public void addRelation(int x0, int y0, int x1, int y1) {
				drawingArea.insert(new Line(40 + x0, 40 + y0, 40 + x1, 40 + y1), 0);
			}
		});
		renderer.setxScale(60);
		renderer.setyScale(60);
		renderer.render();
		
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
