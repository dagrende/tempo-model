package se.findout.tempo.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the participants displayed in a client.
 */
public class ParticipantsModel {
	List<Participant> participants = new ArrayList<Participant>();
	List<Participant> oldParticipants = new ArrayList<Participant>();
		
	private List<PropertyChangeListener> propertyChangeListeners = new ArrayList<PropertyChangeListener>();

	public void clear() {
		oldParticipants.clear();
		oldParticipants.addAll(participants);
		participants.clear();
		firePropertyChange(this, "participants", oldParticipants, participants);
	}
	
	public void addParticipant(Participant participant) {
		oldParticipants.clear();
		oldParticipants.addAll(participants);
		participants.add(participant);
		firePropertyChange(this, "participants", oldParticipants, participants);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeListeners.add(listener);
	}

	private void firePropertyChange(Object source, String propertyName, Object oldValue, Object newValue) {
		for (PropertyChangeListener listener : propertyChangeListeners) {
			listener.propertyChange(new PropertyChangeEvent(source, propertyName, oldValue,
					newValue));
		}
	}

	public List<Participant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Participant> participants) {
		oldParticipants.clear();
		oldParticipants.addAll(participants);
		this.participants.clear();
		this.participants.addAll(participants);
		firePropertyChange(this, "participants", oldParticipants, this.participants);
	}
}
