package se.findout.tempo.client;

import java.io.Serializable;

import se.findout.tempo.client.ModelModel.Box;

public class CreateRectangleCommand implements Command, Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public CreateRectangleCommand() {
	}

	public CreateRectangleCommand(String id, int x, int y, int width, int height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@Override
	public void execute(ModelModel modelModel) {
		modelModel.addBox(new Box(id, x, y, width, height));
	}

	@Override
	public void undo(ModelModel modelModel) {
		System.out.println("ModelEditorView.CreateRectangleCommand.undo()");
		modelModel.deleteBox(id);
	}

	@Override
	public String getDescription() {
		return "Create rectangle";
	}

}