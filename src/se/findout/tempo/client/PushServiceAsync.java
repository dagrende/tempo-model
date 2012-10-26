package se.findout.tempo.client;

import se.findout.tempo.client.model.ChangeInfo;
import se.findout.tempo.client.model.ClearDatabase;
import se.findout.tempo.client.model.ParticipantInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PushServiceAsync {
	void dummy1(AsyncCallback<ChangeInfo> callback);
	void dummy2(AsyncCallback<ParticipantInfo> callback);
	void dummy3(AsyncCallback<ClearDatabase> callback);
}
