package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.examples.shapes.model.EllipticalShape;
import org.eclipse.gef.examples.shapes.model.RectangularShape;
import org.eclipse.gef.examples.shapes.model.ShapesDiagram;
import org.eclipse.gef.examples.shapes.parts.ShapesEditPartFactory;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.vaadin.gwtgraphics.client.VectorObject;

import se.findout.tempo.client.ToolPalette.ToolSelectionEvent;
import se.findout.tempo.client.ToolPalette.ToolSelectionListener;
import se.findout.tempo.client.model.Command;
import se.findout.tempo.client.model.CreateRectangleCommand;
import se.findout.tempo.client.model.DeleteCommand;
import se.findout.tempo.client.model.ModelModel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A model editor for editing a model represented by a ModelModel. Has a palette
 * with commands Rectangle and Delete. Select Rectangle and click to create a
 * box object there. Select Delete and click a box to delete it.
 * 
 * The editor adds a ModelChangeListener to the model in order to display
 * changes.
 * 
 * The editor doesn't change the model directly on user commands, but creates a
 * command object for the intended change and sends it to EditorCommandListeners
 * added to the editor. A mediator is expected to listen to editor coammnds and apply them on the model.
 * 
 * @author dag
 * 
 */
public class ModelEditorView extends FlowPanel implements ToolSelectionListener {
	private List<EditorCommandListener> editorCommandListeners = new ArrayList<ModelEditorView.EditorCommandListener>();
	private List<ModelItem> modelItems = new ArrayList<ModelItem>();
	private int nextId = 1;
	EditDomain editDomain = new EditDomain();

	public ModelEditorView(ModelModel modelModel) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();

		Composite editorControl = new Composite(null, SWT.NONE);
		ScrollingGraphicalViewer sgv = new ScrollingGraphicalViewer();
		sgv.createControl(editorControl);
		sgv.setEditPartFactory(new ShapesEditPartFactory());
		sgv.setEditDomain(editDomain);
		sgv.setContents(createContent());
		editorControl.setSize(700, 300);
		Widget gwtWidget = editorControl.getGwtWidget();
		horizontalPanel.add(gwtWidget);

		add(horizontalPanel);
	}

	private Object createContent() {
		ShapesDiagram diagram = new ShapesDiagram();
		RectangularShape rs = new RectangularShape();
		rs.setSize(new Dimension(75, 75));
		rs.setLocation(new Point(10, 10));
		EllipticalShape es = new EllipticalShape();
		es.setSize(new Dimension(140, 70));
		es.setLocation(new Point(100, 100));
		diagram.addChild(rs);
		diagram.addChild(es);
		return diagram;
	}

	private String createId() {
		while (true) {
			nextId++;
			String idString = Integer.toString(nextId);
			if (getItemById(idString) == null) {
				return idString;
			}
		}
	}

	public void createRectangle(String id, int x, int y, int width, int height) {
		CreateRectangleCommand change = new CreateRectangleCommand(id, x, y,
				width, height);
		fireChange(change);
	}

	public void deleteModelObject(String id) {
		fireChange(new DeleteCommand(id));
	}

	private void fireChange(Command change) {
		for (EditorCommandListener listener : editorCommandListeners) {
			listener.change(change);
		}
	}

	private ModelItem getItemById(String id) {
		for (ModelItem modelItem : modelItems) {
			if (id.equals(modelItem.getId())) {
				return modelItem;
			}
		}
		return null;
	}

	@Override
	public void onSelect(ToolSelectionEvent toolSelectionEvent) {
	}

	public class ModelItem {
		private final String id;
		private final VectorObject vo;

		public ModelItem(String id, VectorObject vo) {
			super();
			this.id = id;
			this.vo = vo;
		}

		public String getId() {
			return id;
		}

		public VectorObject getVo() {
			return vo;
		}
	}

	public ModelItem getItemByVo(Object object) {
		for (ModelItem item : modelItems) {
			if (object == item.vo) {
				return item;
			}
		}
		return null;
	}

	static interface EditorCommandListener {
		void change(Command change);
	}

	public void addModelChangelListener(EditorCommandListener listener) {
		editorCommandListeners.add(listener);
	}

}
