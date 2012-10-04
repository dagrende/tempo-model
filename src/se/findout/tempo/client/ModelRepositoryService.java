package se.findout.tempo.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("modelRepository")
public interface ModelRepositoryService extends RemoteService {
	String addCommand(String versionId, Command command);
}