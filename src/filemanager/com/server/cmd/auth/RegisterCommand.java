package filemanager.com.server.cmd.auth;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.ServerException;
import filemanager.com.server.exception.UserAlreadyExistException;

public class RegisterCommand extends Command {
	@Override
	public boolean validate() throws ServerException {
		System.out.println("[SERVER LOG] REGISTER VALIDATION");
		
		if(!Validator.validateNumberOfArgs(getArgs(), 2)) {
			throw new InvalidNumberOfArgsException(0, getArgs().size());
		}
		
//		if(Authentication.channelIsLoging(Server.input[0])) {
//			Server.output="u are already log in. pls log out first";
//		}
		
		return true;
	}
	
	@Override
	public String exec() throws ServerException {
		System.out.println("[SERVER LOG] REGISTER EXECUTION");
		String username = this.getArgs().get(0);
		if(Authentication.findAccInDatabase(username)) {
			throw new UserAlreadyExistException(username);
		}
		Authentication.addAccountToDatabase(this.getArgs().get(0), this.getArgs().get(1));
		return "Register success";
	}

}
