package se.findout.tempo.client.model;

import java.io.Serializable;

public class ChangeInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String baseVersion;
	private Command change;
	
	public ChangeInfo() {
	}
	
	public ChangeInfo(String baseVersion, Command change) {
		super();
		this.baseVersion = baseVersion;
		this.change = change;
	}
	
	@Override
	public String toString() {
		return "ChangeInfo(" + baseVersion + ", " + change + ")";
	}
	public String getBaseVersion() {
		return baseVersion;
	}

	public void setVersionId(String versionId) {
		this.baseVersion = versionId;
	}

	public Command getChange() {
		return change;
	}

	public void setChange(Command change) {
		this.change = change;
	}

}
