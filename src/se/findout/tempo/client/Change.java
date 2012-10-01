package se.findout.tempo.client;

/**
 * Describes the change from one version to another.
 * @author dag
 */
public interface Change {
	void execute();
	
	void undo();
}
