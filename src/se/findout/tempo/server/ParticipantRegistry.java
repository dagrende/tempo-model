package se.findout.tempo.server;

import java.util.ArrayList;
import java.util.List;

import se.findout.tempo.client.model.Participant;
import se.findout.tempo.client.model.ParticipantInfo;
import se.findout.tempo.client.model.PropertyChangeEvent;
import se.findout.tempo.client.model.PropertyChangeListener;

public class ParticipantRegistry {
	private static ParticipantRegistry participantRegistry = new ParticipantRegistry();
	private ArrayList<Participant> participants = new ArrayList<Participant>();
	private List<PropertyChangeListener> propertyChangeListeners = new ArrayList<PropertyChangeListener>();
	
	public static ParticipantRegistry getInstance() {
		return participantRegistry;
	}
	
	public List<Participant> getParticipants() {
		return participants;
	}
	
	public List<String> getChannelIds() {
		List<String> channelIds = new ArrayList<String>();
		for (Participant participant : participants) {
			channelIds.add(participant.getChannelId());
		}
		return channelIds;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.add(listener);
	}

	void firePropertyChange(Object source, String propertyName, Object oldValue, Object newValue) {
		for (PropertyChangeListener listener : propertyChangeListeners) {
			listener.propertyChange(new PropertyChangeEvent(source, propertyName, oldValue,
					newValue));
		}
		
		for (Participant participant : participants) {
			PushServer.sendMessageByKey(participant.getChannelId(), new ParticipantInfo(participants));
		}
	}

	/**
	 * Create new PArticipant, or set info of existing one, identified by channelId.
	 * @param channelId
	 * @param nickname
	 * @param email
	 * @param userId 
	 */
	public void setParticipantInfo(String channelId, String nickname, String email, String userId) {
		Participant participant = getOrAddParticipant(channelId);
		participant.setNickname(nickname);
		participant.setEmail(email);
		participant.setUserId(userId);
		firePropertyChange(this, "participants", participants, participants);
	}

	private Participant getOrAddParticipant(String channelId) {
		Participant participant = getParticipantByChannelId(channelId);
		if (participant == null) {
			participant = new Participant(channelId);
			participants.add(participant);
		}
		return participant;
	}

	/**
	 * Retrurns participant with specified channelId, or null if not found.
	 * @param channelId key
	 * @return Participant or null
	 */
	private Participant getParticipantByChannelId(String channelId) {
		for (Participant participant : participants) {
			if (channelId.equals(participant.getChannelId())) {
				return participant;
			}
		}
		return null;
	}

	public void connect(String clientId) {
		Participant participant = getOrAddParticipant(clientId);
		participant.setConnected(true);
		firePropertyChange(this, "participants", participants, participants);
	}

	public void disconnect(String clientId) {
		Participant participant = getParticipantByChannelId(clientId);
		if (participant != null) {
			participant.setConnected(false);
			participants.remove(participant);
			firePropertyChange(this, "participants", participants, participants);
		}
	}
}
