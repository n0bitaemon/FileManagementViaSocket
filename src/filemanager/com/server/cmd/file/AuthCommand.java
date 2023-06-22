package filemanager.com.server.cmd.file;

import filemanager.com.server.cmd.Command;

public abstract class AuthCommand extends Command {
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
