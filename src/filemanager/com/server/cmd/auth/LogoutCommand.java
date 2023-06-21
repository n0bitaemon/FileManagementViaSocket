package filemanager.com.server.cmd.auth;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.ServerException;

public class LogoutCommand extends Command {

	@Override
	public boolean validate() throws ServerException {
		//System.out.println("Logout validate()");
		if(!Validator.validateNumberOfArgs(getArgs(), 0)) {
			throw new InvalidNumberOfArgsException(0, getArgs().size());
		}
		
//		if(!Authentication.channelIsLoging(Server.input[0])) {
//			res=false;
//			Server.output="u haven't log in yet";
//		}

		return true;
	}
	
	@Override
	public String exec() throws ServerException {
		//System.out.println("Logout exec()");
		//Server.output = "code reach exec of logout";
//		System.out.println("log out acc "+Server.input[1]+"in channel"+Server.input[0]);
//		Authentication.loging.remove(Server.input[0]);
//		Server.output="Log out success";
//		
//		
//		Enumeration<Object> keys = Authentication.loging.keys();
//
//		while (keys.hasMoreElements()) {
//		    Object k = keys.nextElement();
//		    System.out.println("key: " + k + ", value: " + Authentication.loging.get(k));
//		}
//		//Authentication.loginStatus=false;
		
		return "Logged out";
	}

}
