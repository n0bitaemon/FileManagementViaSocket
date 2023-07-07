package filemanager.com.server.cmd;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.ServerException;

public abstract class AuthCommand extends Command {
	protected String username;
	
	public boolean isAuthenticated() throws ServerException {
		
		if (!Authentication.channelIsLoging(this.remoteAddress)) {
			return false;
		}
		
		this.username = Utils.getCurrentUsername(this.remoteAddress);
		if(username == null) {
			return false;
		}
		
		return true;
	}
}
