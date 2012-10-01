package se.findout.tempo.client;

/**
 * Change that doesn't change anything.
 * @author dag
 *
 */
public class NUllChange implements Change {

	@Override
	public void execute() {
	}

	@Override
	public void undo() {
	}

}
