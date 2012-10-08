package se.findout.tempo.client.model;

import java.io.Serializable;

/**
 * Describes the change from one version to another.
 * @author dag
 */
public interface Command extends Serializable {
	/**
	 * Perform the change.
	 */
	void execute(ModelModel modelModel);
	
	/**
	 * Undo the performed changed.
	 */
	void undo(ModelModel modelModel);

	/**
	 * A human readable description of the change.
	 * @return the description
	 */
	String getDescription();
	
}
