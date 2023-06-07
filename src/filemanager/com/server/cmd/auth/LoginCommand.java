package filemanager.com.server.cmd.auth;

import java.util.List;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;

public class LoginCommand extends Command{
	public static final int NUMBER_OF_ARGS = 2;
	
	public boolean validate() {
		System.out.println("Login validate()");
		List<String> args = getArgs();
		
		//Checking for number of arguments
		if(args.size() != LoginCommand.NUMBER_OF_ARGS) {
			return false;
		}
		
		//Checking for credentials
		
		return true;
	}

	public boolean exec() {
		System.out.println("Login exec()");
		Authentication.login(getArgs().get(0), getArgs().get(1));
		return true;
	}


}
