package se.findout.tempo.client.model;

import java.io.Serializable;

import se.findout.tempo.client.model.ModelModel.Box;

public class DeleteCommand implements Command, Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * id of model object to delete.
	 */
	private String id;
	/**
	 * Deleted box saved for undo.
	 */
	private Box deletedBox;
	
	public DeleteCommand() {
	}
	
	public DeleteCommand(String id) {
		this.id = id;
	}

	@Override
	public void execute(ModelModel modelModel) {
		Box box = modelModel.getBoxById(id);
		deletedBox = new Box(id, box.getX(), box.getY(), box.getWidth(), box.getHeight());
		modelModel.deleteBox(id);
	}

	@Override
	public void undo(ModelModel modelModel) {
		if (deletedBox != null) {
			modelModel.addBox(deletedBox);
		}
	}

	@Override
	public String getDescription() {
		return "Delete object";
	}

	@Override
	public String toString() {
		return "DeleteCommand('" + id + ")";
	}

}