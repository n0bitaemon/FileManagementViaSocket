package filemanager.com.server.cmd.auth;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.exception.InvalidCredentialsException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.ServerException;
import filemanager.com.server.exception.UserAlreadyLoginException;
import filemanager.com.server.exception.UsernameStandard;

public class LoginCommand extends Command {
	

	public boolean validate() throws ServerException {
		// Checking for number of arguments

		if (!Validator.validateNumberOfArgs(getArgs(), 2)) {
			throw new InvalidNumberOfArgsException(2, getArgs().size());
		}

		// Checking for credentials

		if (Authentication.channelIsLoging(getRemoteAddress())) {
			throw new UserAlreadyLoginException(Authentication.session.get(getRemoteAddress()));
		}
		String username = this.getArgs().get(0);
		if (Authentication.accIsLoging(username)) {
			Authentication.session.remove(Authentication.accOfChannel(username));
		}

		return true;
	}

	public String exec() throws ServerException {
		String username = this.getArgs().get(0);
		String password = this.getArgs().get(1);
		
		if (!Authentication.findAccInDatabase(username)) {
			throw new InvalidCredentialsException();
		}

		if (!Authentication.checkPass(username, password)) {
			throw new InvalidCredentialsException();
		}
		
		if (!username.matches("[a-zA-Z0-9]+")) { // no need since user can't create account with this kind of username 
			throw new InvalidCredentialsException();
		}
		
		Authentication.session.put(getRemoteAddress(), username);

		return "Logged in as " + this.getArgs().get(0);
	}

}
