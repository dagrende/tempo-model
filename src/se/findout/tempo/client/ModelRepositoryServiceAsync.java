package se.findout.tempo.client;

import java.util.List;

import se.findout.tempo.client.model.ChangeInfo;
import se.findout.tempo.client.model.Command;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ModelRepositoryServiceAsync {

	void addCommand(String versionId, Command command,
			AsyncCallback<String> callback);

	void getAllChanges(AsyncCallback<List<ChangeInfo>> callback);

}
