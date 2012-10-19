package se.findout.tempo.client.model;

import java.io.Serializable;

public class ChangeInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private int baseVersion;
	private Command change;
	private int changeId;
	
	public ChangeInfo() {
	}
	
	public ChangeInfo(int baseVersion, Command change, int changeId) {
		super();
		this.baseVersion = baseVersion;
		this.change = change;
		this.changeId = changeId;
	}
	
	@Override
	public String toString() {
		return "ChangeInfo(" + baseVersion + ", " + change + ")";
	}
	public int getBaseVersion() {
		return baseVersion;
	}

	public Command getChange() {
		return change;
	}

	public void setChange(Command change) {
		this.change = change;
	}

	public int getChangeId() {
		return changeId;
	}

	public void setChangeId(int changeId) {
		this.changeId = changeId;
	}

}
