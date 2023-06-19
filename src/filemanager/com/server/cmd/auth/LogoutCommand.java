package filemanager.com.server.cmd.auth;

import filemanager.com.server.cmd.Command;

public class LogoutCommand extends Command {

	@Override
	public boolean validate() {
		System.out.println("[SERVER LOG] LOGOUT VALIDATION");
		return true;
	}
	
	@Override
	public String exec() {
		System.out.println("[SERVER LOG] LOGOUT EXECUTION");
		return null;
	}


}
