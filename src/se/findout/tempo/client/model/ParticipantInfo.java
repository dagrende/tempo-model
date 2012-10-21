package se.findout.tempo.client.model;

import java.io.Serializable;
import java.util.List;


public class ParticipantInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Participant> participants;
	
	public ParticipantInfo(List<Participant> participants) {
		super();
		this.setParticipants(participants);
	}

	public ParticipantInfo() {
	}

	public List<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}
	
}
