package se.findout.tempo.client;

import java.util.List;

import se.findout.tempo.client.model.ChangeInfo;
import se.findout.tempo.client.model.Command;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Communicate with the database storing the model, using this service.
 * 
 * @author dag
 *
 */
@RemoteServiceRelativePath("modelRepository")
public interface ModelRepositoryService extends RemoteService {
	int addCommand(String channelId, String documentName, int baseVersion, Command command);
	
	/**
	 * Returns all changes and their baseVersion, stored for the named document so far
	 * @param documentName
	 * @return list of baseVerion/change in chronological order
	 */
	List<ChangeInfo> getAllChanges(String documentName);
}
