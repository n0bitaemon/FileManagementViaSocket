package filemanager.com.server.cmd.auth;

import filemanager.com.server.Response;
import filemanager.com.server.cmd.Command;

public class LogoutCommand extends Command {

	@Override
	public Response validate() {
		System.out.println("[SERVER LOG] LOGOUT VALIDATION");
		return null;
	}
	
	@Override
	public Response exec() {
		System.out.println("[SERVER LOG] LOGOUT EXECUTION");
		return null;
	}


}
