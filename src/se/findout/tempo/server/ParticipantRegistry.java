package se.findout.tempo.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import se.findout.tempo.client.ParticipantInfo;
import se.findout.tempo.client.model.Participant;
import se.findout.tempo.client.model.PropertyChangeEvent;
import se.findout.tempo.client.model.PropertyChangeListener;

public class ParticipantRegistry {
	private final static Logger logger = Logger.getLogger(ParticipantRegistry.class.getName());
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
	
	public void addParticipant(String clientId) {
		participants.add(new Participant(clientId));
		firePropertyChange(this, "participants", participants, participants);
	}


	public void removeParticipant(String clientId) {
		for (Participant participant : participants) {
			if (clientId.equals(participant.getChannelId())) {
				participants.remove(participant);
				firePropertyChange(this, "participants", participants, participants);
				break;
			}
		}
	}
	

	private void addPropertyChangeListener(PropertyChangeListener listener) {
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
}
