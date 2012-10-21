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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ModelRepositoryServiceImpl extends RemoteServiceServlet implements
		ModelRepositoryService {
	private final static Logger logger = Logger.getLogger(ModelRepositoryServiceImpl.class
			.getName());
	private static final long serialVersionUID = 1L;
	private Gson gson = new Gson();
	
	public ModelRepositoryServiceImpl() {
		KindDeleter.deleteAllOfKind("Change");
		KindDeleter.deleteAllOfKind("Document");		
	}

	@Override
	public int addCommand(String channelId, String docPath, int baseVersionId, Command command) {
		logger.log(Level.FINE, "ModelRepositoryServiceImpl.addCommand(" + channelId + ", "
				+ baseVersionId + ", " + command.getDescription() + ")");

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		String json = gson.toJson(command);
		DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
		Transaction tx = dss.beginTransaction();

		try {
			Entity document = getDocumentEntityByName(docPath);
			if (document == null) {
				document = createDocument(tx, docPath, user.getNickname());
			}
			Integer changeId = getPropertyAsInteger(document, "changeId");
			System.out.println("ModelRepositoryServiceImpl.addCommand() document.changeId=" + changeId);
			if (changeId == null) {
				changeId = 1;
			}
			changeId++;
			document.setProperty("changeId", changeId);
			dss.put(tx, document);
			
			Entity changeEntity = new Entity("Change", document.getKey());
			changeEntity.setProperty("changeId", (Integer)changeId);
			changeEntity.setProperty("baseVersion", (Integer)baseVersionId);
			changeEntity.setProperty("changeType", command.getClass().getName());
			changeEntity.setProperty("changeData", json);
			changeEntity.setProperty("createTime", new Date());
			changeEntity.setProperty("creator", user.getNickname());
			dss.put(tx, changeEntity);
			tx.commit();
			sendToParticipants(channelId, baseVersionId, command, changeId);
			return changeId;
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}

	private Integer getPropertyAsInteger(Entity entity, String propertyName) {
		Object value = entity.getProperty(propertyName);
		if (value instanceof Integer) {
			return (Integer)value;
		} else if (value instanceof Long) {
			return Integer.valueOf(((Long)value).intValue());
		} else {
			throw new IllegalArgumentException("value class " + value.getClass().getName()
					+ " of property '" + propertyName + "' of entity " + entity + " not convertible to Integer");
		}
	}

	private void sendToParticipants(String fromChannelId, int versionId, Command change, int changeId) {
		for (String channelId : ParticipantRegistry.getInstance().getChannelIds()) {
			if (!channelId.equals(fromChannelId)) {
				PushServer.sendMessageByKey(channelId, new ChangeInfo(versionId, change, changeId));
			}
		}
	}

	/**
	 * Returns the unique document having the given name.
	 * 
	 * 
	 * @param name
	 * @return
	 */
	private Entity getDocumentEntityByName(String name) {
		List<Entity> docs = findDocumentsByName(name);
		if (docs.size() > 1) {
			logger.log(Level.WARNING, "Multiple documents with same name: '" + name + "'");
		}
		if (docs.isEmpty()) {
			return null;
		}
		return docs.get(0);
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
		Query query = new Query("Document").setFilter(
				new FilterPredicate("name", FilterOperator.EQUAL, name));
		List<Entity> docsWithKey = DatastoreServiceFactory.getDatastoreService().prepare(query)
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
	private Entity createDocument(Transaction tx, String docName, String userNickName) {
		Entity documentEntity = new Entity("Document");
		documentEntity.setProperty("name", docName);
		documentEntity.setProperty("createTime", new Date());
		documentEntity.setProperty("creator", userNickName);
		documentEntity.setProperty("changeId", (Integer)1);
		DatastoreService dss = DatastoreServiceFactory.getDatastoreService();
		dss.put(tx, documentEntity);
		return documentEntity;
	}

	/**
	 * Returns all Change objects with the named document as parent.
	 */
	@Override
	public List<ChangeInfo> getAllChanges(String docName) {
		Entity document = getDocumentEntityByName(docName);
		if (document != null) {
			Query query = new Query("Change").setAncestor(document.getKey()).addSort("createTime",
					Query.SortDirection.ASCENDING);
			Iterable<Entity> channelEntities = DatastoreServiceFactory.getDatastoreService()
					.prepare(query).asIterable();
			List<ChangeInfo> changeInfos = new ArrayList<ChangeInfo>();
			for (Entity entity : channelEntities) {
				int changeId = getPropertyAsInteger(entity, "changeId");
				int baseVersionId = (int)(long)(Long) entity.getProperty("baseVersion");
				String changeType = (String) entity.getProperty("changeType");
				String changeData = (String) entity.getProperty("changeData");
				try {
					changeInfos.add(new ChangeInfo(baseVersionId, (Command) gson.fromJson(changeData,
							Class.forName(changeType)), changeId));
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
					throw new IllegalArgumentException("Invalid json syntax in db kind=" + entity.getKind() + " key=" + entity.getKey(), e);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					throw new IllegalArgumentException("Invalid json syntax in db kind=" + entity.getKind() + " key=" + entity.getKey() + " changeType=" + changeType, e);
				}
			}

			return changeInfos;
		} else {
			return Collections.emptyList();
		}
	}

}
