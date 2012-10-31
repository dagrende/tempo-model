package se.findout.tempo.client.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Value object describing a change, to store in database or transferring through a channel.
 */
public class ChangeInfo implements IsSerializable {
	private static final long serialVersionUID = 3L;
	private int baseVersion;
	private Command change;
	private int changeId;
	private String creator;
	
	public ChangeInfo() {
	}
	
	public ChangeInfo(int baseVersion, Command change, int changeId, String creator) {
		super();
		this.baseVersion = baseVersion;
		this.change = change;
		this.changeId = changeId;
		this.creator = creator;
	}
	
	@Override
	public String toString() {
		return "ChangeInfo(id=" + changeId + ", baseId=" + baseVersion + ", " + change + ")";
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

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

}
