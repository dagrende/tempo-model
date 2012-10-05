package se.findout.tempo.client;

import java.io.Serializable;

public class ChangeInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	String versionId;
	Command change;
	
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
}
