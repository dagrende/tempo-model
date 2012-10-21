package se.findout.tempo.client;

import se.findout.tempo.client.model.ChangeInfo;
import se.findout.tempo.client.model.ParticipantInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * A GWT RPC service interface for RPC calls which are pushed to this client.
 * <p>
 * This interface is odd in that the client doesn't actually make calls through
 * this interface to the server. Instead the server uses server-side push to
 * send GWT RPC encoded data to the client via the Google Channel API. The
 * definition of this interface helps to ensure that all the correct
 * de-serialization code is generated for the client. A call to GWT.create on
 * this service must be made to ensure the de-serialization code is actually
 * generated.
 */
@RemoteServiceRelativePath("push_service")
public interface PushService extends RemoteService {
	/**
	 * A dummy method ensuring that ChangeInfo and all its subclasses are client
	 * serializable.
	 */
	ChangeInfo receiveMessage();
	ParticipantInfo receiveMessage2();
}
