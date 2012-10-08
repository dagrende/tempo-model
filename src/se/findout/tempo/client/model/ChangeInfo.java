package se.findout.tempo.client.model;

import java.io.Serializable;

public class ChangeInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String versionId;
	private Command change;
	
	public ChangeInfo() {
	}
	
	public ChangeInfo(String versionId, Command change) {
		super();
		this.versionId = versionId;
		this.change = change;
	}
	
	@Override
	public String toString() {
		return "ChangeInfo(" + versionId + ", " + change + ")";
	}
	public String getVersionId() {
		return versionId;
	}

	public void setVersionId(String versionId) {
		this.versionId = versionId;
	}

	public Command getChange() {
		return change;
	}

	public void setChange(Command change) {
		this.change = change;
	}

}
