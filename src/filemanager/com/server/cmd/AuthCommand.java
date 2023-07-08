package filemanager.com.server.cmd;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.common.Utils;

public abstract class AuthCommand extends Command {
	protected String username;
	
	/**
	 * Check if a user is logged in or not
	 * <br>
	 * If the user is in a session, then set username for the command
	 * @return true, false
	 */
	public boolean isAuthenticated() {
		if (!Authentication.channelIsLoging(this.remoteAddress)) {
			return false;
		}
		
		this.username = Utils.getCurrentUsername(this.remoteAddress);
		return this.username != null;
	}
}
