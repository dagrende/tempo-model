package se.findout.tempo.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ModelRepositoryServiceAsync {

	void addCommand(String versionId, Command command,
			AsyncCallback<String> callback);

}
