package se.findout.tempo.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelModel {
	private Map<String, Box> boxes = new HashMap<String, ModelModel.Box>();
	private List<ModelChangeListener> changeListeners = new ArrayList<ModelModel.ModelChangeListener>();
	
	public static class Box {
		private String id;
		private int x;
		private int y;
		private int width;
		private int height;
		
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
	}

	public void addBox(String id, int x, int y, int width, int height) {
		Box box = new Box(id, x, y, width, height);
		boxes.put(id, box);
		fireBoxAdded(box);
	}

	public void deleteBox(String id) {
		System.out.println("ModelModel.deleteBox(" + id + ")");
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
		System.out.println("ModelModel.fireBoxDeleted(" + id + ")");
		for (ModelChangeListener listener : changeListeners) {
			listener.deleteBox(id);
		}
	}

}
