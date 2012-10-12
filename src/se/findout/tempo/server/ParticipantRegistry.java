package se.findout.tempo.server;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;

public class ParticipantRegistry {
	private final static Logger logger = Logger.getLogger(ParticipantRegistry.class.getName());

	public static void add(HttpServletRequest req) throws IOException {
		System.out.println("ParticipantRegistry.add()");
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ChannelPresence presence = channelService.parsePresence(req);
		
		Entity documentEntity = new Entity("Participant", presence.clientId());
		documentEntity.setProperty("createTime", new Date());
		DatastoreServiceFactory.getDatastoreService().put(documentEntity);
	}

	public static void remove(HttpServletRequest req) throws IOException {
		System.out.println("ParticipantRegistry.remove()");
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ChannelPresence presence = channelService.parsePresence(req);
		
		DatastoreServiceFactory.getDatastoreService().delete(KeyFactory.createKey("Participant", presence.clientId()));
	}
}
