package se.findout.tempo.client;

import java.util.List;

import se.findout.tempo.client.model.ChangeInfo;
import se.findout.tempo.client.model.Command;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ModelRepositoryServiceAsync {

	void addCommand(String channelId, String documentName, int baseVersion, Command command,
			AsyncCallback<Integer> callback);

	void getAllChanges(String docPath, AsyncCallback<List<ChangeInfo>> callback);

	void clearDatabase(AsyncCallback<Void> callback);

}
