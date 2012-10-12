package se.findout.tempo.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import se.findout.tempo.client.ModelRepositoryService;
import se.findout.tempo.client.model.ChangeInfo;
import se.findout.tempo.client.model.Command;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ModelRepositoryServiceImpl extends RemoteServiceServlet implements
		ModelRepositoryService {
	public class ChangeInfo2 {
		private String baseVersion;
		private String commandType;
		private Command command;

		public ChangeInfo2(String baseVersion, String commandType, Command command) {
			this.baseVersion = baseVersion;
			this.commandType = commandType;
			this.command = command;
		}

	}

	private final static Logger logger = Logger
			.getLogger(ModelRepositoryServiceImpl.class.getName());
	private static final long serialVersionUID = 1L;
	private Gson gson = new Gson();

	@Override
	public String addCommand(String channelId, String docPath,
			String versionId, Command command) {
		logger.log(Level.FINE,
				"ModelRepositoryServiceImpl.addCommand(" + channelId + ", "
						+ versionId + ", " + command.getDescription() + ")");

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		Key documentKey = getDocumentKeyByName(docPath);
		if (documentKey == null) {
			documentKey = createDocument(docPath, user.getNickname());
		}

		Entity changeEntity = new Entity("Change", documentKey);
		changeEntity.setProperty("baseVersion", versionId);
		changeEntity.setProperty("changeType", command.getClass().getName());
		String json = gson.toJson(command);
		changeEntity.setProperty("changeData", json);
		changeEntity.setProperty("createTime", new Date());
		changeEntity.setProperty("creator", user.getNickname());
		DatastoreServiceFactory.getDatastoreService().put(changeEntity);

		sendToParticipants(channelId, versionId, command);

		return versionId;
	}

	private void sendToParticipants(String fromChannelId, String versionId, Command change) {
		Query query = new Query("Participant").setKeysOnly();
		Iterable<Entity> iterable = DatastoreServiceFactory
				.getDatastoreService().prepare(query).asIterable();
		System.out
				.println("ModelRepositoryServiceImpl.sendToParticipants() send to channelId:");
		for (Entity entity : iterable) {
			String channelId = entity.getKey().getName();
			if (!channelId.equals(fromChannelId)) {
				ChannelService channelService = ChannelServiceFactory
						.getChannelService();
				ChangeInfo2 changeInfo2 = new ChangeInfo2(versionId, change.getClass().getName(), change);
				String json = gson.toJson(changeInfo2);
				System.out.println("  " + channelId + " " + json);
				channelService.sendMessage(new ChannelMessage(channelId, json));
			}
		}
	}

	/**
	 * Returns the key of the unique document having the given name.
	 * 
	 * @param name
	 * @return
	 */
	private Key getDocumentKeyByName(String name) {
		List<Entity> docsWithKey = findDocumentsByName(name);
		if (docsWithKey.size() > 1) {
			logger.log(Level.WARNING, "Multiple documents with same name: '"
					+ name + "'");
		}
		if (docsWithKey.isEmpty()) {
			return null;
		}
		return docsWithKey.get(0).getKey();
	}

	/**
	 * Searches for Documents with the given name and returns maximum two of
	 * them.
	 * 
	 * @param name
	 * @return
	 */
	private List<Entity> findDocumentsByName(String name) {
		int slashPos = name.indexOf("/");
		if (slashPos != -1) {
			throw new IllegalArgumentException(
					"folders not implemented yet - use a document name without slashes");
		}
		Query query = new Query("Document").setKeysOnly().setFilter(
				new FilterPredicate("name", FilterOperator.EQUAL, name));
		List<Entity> docsWithKey = DatastoreServiceFactory
				.getDatastoreService().prepare(query)
				.asList(FetchOptions.Builder.withLimit(2));
		return docsWithKey;
	}

	/**
	 * Create a new document with the given name.
	 * 
	 * @param name
	 *            document name
	 * @param userNickName
	 *            will be creator in the new document
	 * @return the created document key
	 */
	private Key createDocument(String docName, String userNickName) {
		Entity documentEntity = new Entity("Document");
		documentEntity.setProperty("name", docName);
		documentEntity.setProperty("createTime", new Date());
		documentEntity.setProperty("creator", userNickName);
		return DatastoreServiceFactory.getDatastoreService()
				.put(documentEntity);
	}

	/**
	 * Returns all Change objects with the named document as parent.
	 */
	@Override
	public List<ChangeInfo> getAllChanges(String docName) {
		Key documentKey = getDocumentKeyByName(docName);
		if (documentKey != null) {
			Query query = new Query("Change").setAncestor(documentKey).addSort(
					"createTime", Query.SortDirection.ASCENDING);
			Iterable<Entity> channelEntities = DatastoreServiceFactory
					.getDatastoreService().prepare(query).asIterable();
			List<ChangeInfo> changeInfos = new ArrayList<ChangeInfo>();
			for (Entity entity : channelEntities) {
				String versionId = (String) entity.getProperty("baseVersion");
				String changeType = (String) entity.getProperty("changeType");
				String changeData = (String) entity.getProperty("changeData");
				try {
					changeInfos.add(new ChangeInfo(versionId, (Command) gson
							.fromJson(changeData, Class.forName(changeType))));
				} catch (JsonSyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// changeInfos.add(new ChangeInfo("22.22", new
				// DeleteCommand("34")));

			}

			return changeInfos;
		} else {
			return Collections.emptyList();
		}
	}

}
