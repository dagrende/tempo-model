package se.findout.tempo.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import se.findout.tempo.client.model.Participant;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class ParticipantRegistry {
	private final static Logger logger = Logger.getLogger(ParticipantRegistry.class.getName());
	private static ParticipantRegistry participantRegistry = new ParticipantRegistry();
	
	public static ParticipantRegistry getInstance() {
		return participantRegistry;
	}
	
	public static List<Participant> getParticipants() {
		Query query = new Query("Participant").setKeysOnly();
		Iterable<Entity> iterable = DatastoreServiceFactory
				.getDatastoreService().prepare(query).asIterable();
		ArrayList<Participant> participants = new ArrayList<Participant>();
		for (Entity entity : iterable) {
			String channelId = entity.getKey().getName();
			participants.add(new Participant(channelId));
		}
		return participants;
	}
	
	public List<String> getChannelIds() {
		Query query = new Query("Participant").setKeysOnly();
		Iterable<Entity> iterable = DatastoreServiceFactory
				.getDatastoreService().prepare(query).asIterable();
		List<String> channelIds = new ArrayList<String>();
		for (Entity entity : iterable) {
			channelIds.add(entity.getKey().getName());
		}
		return channelIds;
	}
	
	public void addParticipant(String clientId) {
		Entity documentEntity = new Entity("Participant", clientId);
		documentEntity.setProperty("createTime", new Date());
		DatastoreServiceFactory.getDatastoreService().put(documentEntity);
	}


	public void removeParticipant(String clientId) {
		DatastoreServiceFactory.getDatastoreService().delete(KeyFactory.createKey("Participant", clientId));
	}
}
