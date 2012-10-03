package se.findout.tempo.client;

/**
 * Describes the change from one version to another.
 * @author dag
 */
public interface Change {
	/**
	 * Perform the change.
	 */
	void execute();
	
	/**
	 * Undo the performed changed.
	 */
	void undo();

	/**
	 * A human readable description of the change.
	 * @return the description
	 */
	String getDescription();
}
