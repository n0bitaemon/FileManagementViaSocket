package filemanager.com.server.cmd.auth;

import java.util.List;

import filemanager.com.server.Response;
import filemanager.com.server.cmd.Command;

public class LoginCommand extends Command{
	public static final int NUMBER_OF_ARGS = 2;
	
	public Response validate() {
		System.out.println("[SERVER LOG] LOGIN VALIDATION");
		List<String> args = getArgs();
		
		//Checking for number of arguments
		if(args.size() != LoginCommand.NUMBER_OF_ARGS) {
			
		}
		
		//Checking for credentials
		
		return null;
	}

	public Response exec() {
		System.out.println("[SERVER LOG] LOGIN EXECUTION");
		return null;
	}


}
