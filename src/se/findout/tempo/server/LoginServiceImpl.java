package se.findout.tempo.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import se.findout.tempo.client.login.LoginInfo;
import se.findout.tempo.client.login.LoginService;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {
	private static final long serialVersionUID = 1L;
	private static SimpleDateFormat channelIdTimeFormat = new SimpleDateFormat("HHmmss");

	public LoginInfo login(String requestUri) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		LoginInfo loginInfo = new LoginInfo();

		if (user != null) {
			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));

			String userId = user.getUserId();
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			String channelId = "tempo-model-" + channelIdTimeFormat.format(new Date()) + "-" + userId;
			loginInfo.setChannelId(channelId);
			String token = channelService.createChannel(channelId);
			loginInfo.setChannelToken(token);
			
			
			ParticipantRegistry participantRegistry = ParticipantRegistry.getInstance();
			participantRegistry.setParticipantInfo(channelId, user.getNickname(), user.getEmail(), user.getUserId());
			loginInfo.setParticipants(participantRegistry.getParticipants());

		} else {
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return loginInfo;
	}

}