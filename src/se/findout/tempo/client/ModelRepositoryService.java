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
	/**
	 * Adds the supplied command changing a version of a model, and inform other clients of the change
	 * @param fromChannelId the channel to the client this command comes from
	 * @param documentName identifies the model to act on
	 * @param baseVersion version before this command
	 * @param command the command changing the version
	 * @return id of the new version resulting from the change
	 */
	int addCommand(String fromChannelId, String documentName, int baseVersion, Command command);
	
	/**
	 * Returns info about all changes with versionId greater than the specified latestExistingVersion, stored for the named document.
	 * @param documentName
	 * @param latestExistingVersion id of version to get changes after
	 * @return list of change info in chronological order
	 */
	List<ChangeInfo> getChangesAfterVersion(String documentName, int latestExistingVersion);
	
	/**
	 * Clears the database and informs clients about it.
	 * This change is not a normal model change added to the versions - it removes all versions.
	 */
	void clearDatabase();
}
