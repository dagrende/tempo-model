package se.findout.tempo.client;

import se.findout.tempo.client.model.ChangeInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PushServiceAsync {

	void receiveMessage(AsyncCallback<ChangeInfo> callback);

	void receiveMessage2(AsyncCallback<ParticipantInfo> callback);

}
