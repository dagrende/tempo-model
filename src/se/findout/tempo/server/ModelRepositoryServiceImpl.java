package se.findout.tempo.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import se.findout.tempo.client.ModelRepositoryService;
import se.findout.tempo.client.model.ChangeInfo;
import se.findout.tempo.client.model.Command;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ModelRepositoryServiceImpl extends RemoteServiceServlet implements ModelRepositoryService {
	private static final long serialVersionUID = 1L;
	private Gson gson = new Gson();

	@Override
	public String addCommand(String versionId, Command command) {
		System.out.println("ModelRepositoryServiceImpl.addCommand(" + versionId + ", " + command.getDescription() + ")");
	    UserService userService = UserServiceFactory.getUserService();
	    User user = userService.getCurrentUser();

		Entity changeEntity = new Entity("Change");
		changeEntity.setProperty("version", versionId);
		changeEntity.setProperty("changeType", command.getClass().getName());
		changeEntity.setProperty("changeData", gson.toJson(command));
		changeEntity.setProperty("time", new Date());
		changeEntity.setProperty("user", user.getNickname());
		DatastoreServiceFactory.getDatastoreService().put(changeEntity);
		
		return "successor of " + versionId;
	}

	@Override
	public List<ChangeInfo> getAllChanges() {
		Query query = new Query("Change").addSort("time", Query.SortDirection.ASCENDING);
	    Iterable<Entity> channelEntities = DatastoreServiceFactory.getDatastoreService().prepare(query).asIterable();
	    List<ChangeInfo> changeInfos = new ArrayList<ChangeInfo>();
	    for (Entity entity : channelEntities) {
			String versionId = (String) entity.getProperty("version");
			String changeType = (String) entity.getProperty("changeType");
			String changeData = (String) entity.getProperty("changeData");
			try {
				changeInfos.add(new ChangeInfo(versionId, (Command)gson.fromJson(changeData, Class.forName(changeType))));
			} catch (JsonSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			changeInfos.add(new ChangeInfo("22.22", new DeleteCommand("34")));

		}
		
		return changeInfos;
	}

}
