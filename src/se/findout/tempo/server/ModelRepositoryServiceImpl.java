package se.findout.tempo.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import se.findout.tempo.client.Command;
import se.findout.tempo.client.ModelRepositoryService;

public class ModelRepositoryServiceImpl extends RemoteServiceServlet implements ModelRepositoryService {
	private static final long serialVersionUID = 1L;

	@Override
	public String addCommand(String versionId, Command command) {
		System.out.println("ModelRepositoryServiceImpl.addCommand(" + versionId + ", " + command.getDescription() + ")");
		return "successor of " + versionId;
	}

}
