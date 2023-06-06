package filemanager.com.server.cmd.auth;

import filemanager.com.server.cmd.Command;

public class LogoutCommand extends Command {

	@Override
	public boolean exec() {
		System.out.println("Logout exec()");
		return false;
	}

	@Override
	public boolean validate() {
		System.out.println("Logout validate()");
		return false;
	}

}
