package se.findout.tempo.client.model;

import java.io.Serializable;

public class Participant implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userAlias;
	private String userId;
	private String channelId;
	private String docPath;
	private boolean connected = false;
	
	public Participant() {
	}
	
	public Participant(String channelId) {
		this.channelId = channelId;
	}
	
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getDocPath() {
		return docPath;
	}
	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	public String getUserAlias() {
		return userAlias;
	}

	public void setUserAlias(String userAlias) {
		this.userAlias = userAlias;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
