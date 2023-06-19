package filemanager.com.server.cmd.auth;

import java.util.List;

import filemanager.com.server.cmd.Command;

public class LoginCommand extends Command{
	public static final int NUMBER_OF_ARGS = 2;
	
	public boolean validate() {
		System.out.println("[SERVER LOG] LOGIN VALIDATION");
		List<String> args = getArgs();
		
		//Checking for number of arguments
		if(args.size() != LoginCommand.NUMBER_OF_ARGS) {
			
		}
		
		//Checking for credentials
		
		return true;
	}

	public String exec() {
		System.out.println("[SERVER LOG] LOGIN EXECUTION");
		return "Success";
	}


}
