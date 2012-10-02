package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.VectorObject;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import se.findout.tempo.client.ToolPalette.ToolSelectionEvent;
import se.findout.tempo.client.ToolPalette.ToolSelectionListener;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ModelEditorView extends FlowPanel implements ToolSelectionListener {
	private List<ModelChangeListener> modelChangeListeners = new ArrayList<ModelEditorView.ModelChangeListener>();
	private DrawingArea drawingArea;
	private List<ModelItem> modelItems = new ArrayList<ModelItem>();
	private int nextId = 1;
	private ClickHandler modelItemClickHandler = new ModelItemClickHandler();
	private ToolPalette toolPalette;

	public ModelEditorView() {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		
		toolPalette = new ToolPalette();
		toolPalette.addTool("selection", "Selection");
		toolPalette.addTool("rectangle", "Rectangle");
		toolPalette.addTool("relation", "Relation");
		toolPalette.addTool("delete", "Delete");
		toolPalette.addSelectionListener(this);
		horizontalPanel.add(toolPalette);
		
		drawingArea = new DrawingArea(400, 400);
		drawingArea.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if ("selection".equals(toolPalette.getSelectedTool())) {
				} else if ("rectangle".equals(toolPalette.getSelectedTool())) {
					createRectangle(createId(), event.getX(), event.getY(), 50, 50);
				}
			}
		});
		horizontalPanel.add(drawingArea);
		
		add(horizontalPanel);
		toolPalette.selectTool("selection");
	}
	
	private String createId() {
		int id = nextId++;
		return Integer.toString(id);
	}

	public void createRectangle(String id, int x, int y, int width, int height) {
		CreateRectangleChange change = new CreateRectangleChange(id, x, y, width, height);
		fireChange(change);
	}

	public void deleteModelObject(String id) {
		fireChange(new DeleteCommand(id));
	}

	private void fireChange(Change change) {
		for (ModelChangeListener listener : modelChangeListeners) {
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

	private final class ModelItemClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			ModelItem item = getItemByVo(event.getSource());
			if ("delete".equals(toolPalette.getSelectedTool())) {
				if (item != null) {
					deleteModelObject(item.getId());
				}
			}
		}
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
	
	static interface ModelChangeListener {
		void change(Change change);
	}
	
	public void addModelChangelListener(ModelChangeListener listener) {
		modelChangeListeners.add(listener);
	}

	public class CreateRectangleChange implements Change {
		private String id;
		private int x;
		private int y;
		private int width;
		private int height;
		private Rectangle rectangle;
		private ModelItem modelItem;

		public CreateRectangleChange(String id, int x, int y, int width, int height) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		@Override
		public void execute() {
			rectangle = new Rectangle(x, y, width, height);
			rectangle.addClickHandler(modelItemClickHandler);
			modelItem = new ModelItem(id, rectangle);
			modelItems.add(modelItem);
			drawingArea.add(rectangle);
		}

		@Override
		public void undo() {
			drawingArea.remove(rectangle);
			modelItems.remove(modelItem);
		}

	}

	public class DeleteCommand implements Change {
		/**
		 * id of model object to delete.
		 */
		private String id;
		private ModelItem deletedItem;

		public DeleteCommand(String id) {
			this.id = id;
		}

		@Override
		public void execute() {
			deletedItem = getItemById(id);
			if (deletedItem != null) {
				drawingArea.remove(deletedItem.getVo());
				modelItems.remove(deletedItem);
			}
		}

		@Override
		public void undo() {
			if (deletedItem != null) {
				drawingArea.add(deletedItem.getVo());
				modelItems.add(deletedItem);
			}
		}

	}

}
