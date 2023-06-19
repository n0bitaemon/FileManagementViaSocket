package filemanager.com.server.cmd.auth;

import filemanager.com.server.cmd.Command;

public class RegisterCommand extends Command {

	@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		System.out.println("[SERVER LOG] REGISTER VALIDATION");
		return true;
	}
	
	@Override
	public String exec() {
		// TODO Auto-generated method stub
		System.out.println("[SERVER LOG] REGISTER EXECUTION");
		return null;
	}


}
