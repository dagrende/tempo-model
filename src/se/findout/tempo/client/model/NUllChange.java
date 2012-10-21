package se.findout.tempo.client.model;


/**
 * A change command that doesn't change anything.
 * @author dag
 *
 */
public class NUllChange implements Command {
	private static final long serialVersionUID = 1L;
	
	public NUllChange() {
	}

	@Override
	public void execute(ModelModel modelModel) {
	}

	@Override
	public void undo(ModelModel modelModel) {
	}

	@Override
	public String getDescription() {
		return "No change";
	}

}
