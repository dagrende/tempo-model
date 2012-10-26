package se.findout.tempo.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a model consisting of Box objects. Each box have id, position and size.
 * Add a ModelChangeListener to get informed of added and deleted boxes.
 */
public class ModelModel {
    private final static Logger logger = Logger.getLogger(ModelModel.class.getName());
	private Map<String, Box> boxes = new HashMap<String, ModelModel.Box>();
	private List<ModelChangeListener> changeListeners = new ArrayList<ModelModel.ModelChangeListener>();
	
	public static class Box implements Serializable {
		private static final long serialVersionUID = 1L;
		private String id;
		private int x;
		private int y;
		private int width;
		private int height;
		
		public Box() {
		}
		
		public Box(String id, int x, int y, int width, int height) {
			super();
			this.id = id;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public String getId() {
			return id;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		@Override
		public String toString() {
			return "Box('" + id + "', " + x + ", " + y + ", " + width + ", " + height + ")";
		}
}

	public void addBox(Box box) {
		logger.log(Level.FINE, "ModelModel.addBox(" + box + ")");
		boxes.put(box.getId(), box);
		fireBoxAdded(box);
	}

	public void deleteBox(String id) {
		logger.log(Level.FINE, "ModelModel.deleteBox(" + id + ")");
		Box box = boxes.get(id);
		if (box != null) {
			boxes.remove(id);
			fireBoxDeleted(id);
		}
	}

	public void addChangeListener(ModelChangeListener listener) {
		changeListeners.add(listener);
	}
	
	public interface ModelChangeListener {
		void addBox(Box box);
		void deleteBox(String id);
	}
	
	private void fireBoxAdded(Box box) {
		for (ModelChangeListener listener : changeListeners) {
			listener.addBox(box);
		}
	}
	
	private void fireBoxDeleted(String id) {
		logger.log(Level.FINE, "ModelModel.fireBoxDeleted(" + id + ")");
		for (ModelChangeListener listener : changeListeners) {
			listener.deleteBox(id);
		}
	}

	public Box getBoxById(String id) {
		return boxes.get(id);
	}

	public void clear() {
		boxes.clear();
	}

}
