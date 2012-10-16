package se.findout.tempo.client.model;

import java.io.Serializable;

public class Participant implements Serializable {
	private static final long serialVersionUID = 1L;
	private String nickname;
	private String userId;
	private String channelId;
	private String docPath;
	private boolean connected = false;
	private String email;
	
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

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
