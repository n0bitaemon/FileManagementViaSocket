package filemanager.com.server.cmd;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.common.Utils;

public abstract class AuthCommand extends Command {
	protected String username;
	
	public boolean isAuthenticated() {
		
		if (!Authentication.channelIsLoging(this.remoteAddress)) {
			return false;
		}
		
		this.username = Utils.getCurrentUsername(this.remoteAddress);
		return this.username != null;
	}
}
