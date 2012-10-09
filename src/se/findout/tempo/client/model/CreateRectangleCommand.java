package se.findout.tempo.client.model;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.findout.tempo.client.model.ModelModel.Box;


public class CreateRectangleCommand implements Command, Serializable {
    private final static Logger logger = Logger.getLogger(CreateRectangleCommand.class.getName());
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
		logger.log(Level.FINE, "");
		modelModel.addBox(new Box(id, x, y, width, height));
	}

	@Override
	public void undo(ModelModel modelModel) {
		logger.log(Level.FINE, "");
		modelModel.deleteBox(id);
	}

	@Override
	public String getDescription() {
		return "Create rectangle";
	}

	@Override
	public String toString() {
		return "CreateRectangleCommand('" + id + "', " + x + ", " + y + ", " + width + ", " + height + ")";
	}
}