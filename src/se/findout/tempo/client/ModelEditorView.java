package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.VectorObject;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import se.findout.tempo.client.ModelModel.Box;
import se.findout.tempo.client.ModelModel.ModelChangeListener;
import se.findout.tempo.client.ToolPalette.ToolSelectionEvent;
import se.findout.tempo.client.ToolPalette.ToolSelectionListener;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ModelEditorView extends FlowPanel implements ToolSelectionListener {
	private List<EditorCommandListener> editorCommandListeners = new ArrayList<ModelEditorView.EditorCommandListener>();
	private DrawingArea drawingArea;
	private List<ModelItem> modelItems = new ArrayList<ModelItem>();
	private ModelModel modelModel;
	private int nextId = 1;
	private ClickHandler modelItemClickHandler = new ModelItemClickHandler();
	private ToolPalette toolPalette;

	public ModelEditorView(ModelModel modelModel) {
		this.modelModel = modelModel;
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		
		toolPalette = new ToolPalette();
		toolPalette.addTool("rectangle", "Rectangle");
		toolPalette.addTool("delete", "Delete");
		toolPalette.addSelectionListener(this);
		horizontalPanel.add(toolPalette);
		
		drawingArea = new DrawingArea(400, 400);
		drawingArea.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if ("rectangle".equals(toolPalette.getSelectedTool())) {
					createRectangle(createId(), event.getX(), event.getY(), 50, 50);
				}
			}
		});
		horizontalPanel.add(drawingArea);
		
		add(horizontalPanel);
		toolPalette.selectTool("rectangle");
		
		modelModel.addChangeListener(new ModelModel.ModelChangeListener() {
			@Override
			public void addBox(Box box) {
				Rectangle rectangle = new Rectangle(box.getX(), box.getY(), box.getWidth(), box.getHeight());
				rectangle.addClickHandler(modelItemClickHandler);
				ModelItem modelItem = new ModelItem(box.getId(), rectangle);
				modelItems.add(modelItem);
				drawingArea.add(rectangle);
			}
			
			@Override
			public void deleteBox(String id) {
				ModelItem deletedItem = getItemById(id);
				if (deletedItem != null) {
					System.out
							.println("ModelEditorView.ModelEditorView(...).new ModelChangeListener() {...}.deleteBox(" + id + ")");
					drawingArea.remove(deletedItem.getVo());
					modelItems.remove(deletedItem);
				}
			}
			
		});
	}
	
	private String createId() {
		int id = nextId++;
		return Integer.toString(id);
	}

	public void createRectangle(String id, int x, int y, int width, int height) {
		CreateRectangleCommand change = new CreateRectangleCommand(id, x, y, width, height);
		fireChange(change);
	}

	public void deleteModelObject(String id) {
		fireChange(new DeleteCommand(id));
	}

	private void fireChange(Change change) {
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
	
	static interface EditorCommandListener {
		void change(Change change);
	}
	
	public void addModelChangelListener(EditorCommandListener listener) {
		editorCommandListeners.add(listener);
	}

	public class CreateRectangleCommand implements Change {
		private String id;
		private int x;
		private int y;
		private int width;
		private int height;
		private Rectangle rectangle;

		public CreateRectangleCommand(String id, int x, int y, int width, int height) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		@Override
		public void execute() {
			modelModel.addBox(id, x, y, width, height);
		}

		@Override
		public void undo() {
			System.out.println("ModelEditorView.CreateRectangleCommand.undo()");
			modelModel.deleteBox(id);
		}

		@Override
		public String getDescription() {
			return "Create rectangle";
		}

	}

	public class DeleteCommand implements Change {
		/**
		 * id of model object to delete.
		 */
		private String id;
		private Box deletedBox;

		public DeleteCommand(String id) {
			this.id = id;
		}

		@Override
		public void execute() {
			ModelItem item = getItemById(id);
			Rectangle rectangle = (Rectangle) item.getVo();
			deletedBox = new Box(id, rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
			modelModel.deleteBox(id);
		}

		@Override
		public void undo() {
			if (deletedBox != null) {
				modelModel.addBox(deletedBox.getId(), deletedBox.getX(), deletedBox.getY(), deletedBox.getWidth(), deletedBox.getHeight());
			}
		}

		@Override
		public String getDescription() {
			return "Delete object";
		}

	}

}
