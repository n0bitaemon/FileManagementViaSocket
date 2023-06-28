package filemanager.com.server.cmd.auth;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.exception.InvalidCredentialsException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.ServerException;
import filemanager.com.server.exception.UserAlreadyLoginException;

public class LoginCommand extends Command {

	public boolean validate() throws ServerException {
		// Checking for number of arguments

		if (!Validator.validateNumberOfArgs(this.args, 2)) {
			throw new InvalidNumberOfArgsException(2, this.args.size());
		}

		// Checking for credentials

		if (Authentication.channelIsLoging(this.remoteAddress)) {
			throw new UserAlreadyLoginException(Authentication.session.get(this.remoteAddress));
		}
		
		String username = this.args.get(0);
		if (Authentication.accIsLoging(username)) {
			Authentication.session.remove(Authentication.accOfChannel(username));
		}

		return true;
	}

	public String exec() throws ServerException {
		String username = this.args.get(0);
		String password = this.args.get(1);

		System.out.println(username + password);
		if (!Authentication.findAccInDatabase(username)) {
			throw new InvalidCredentialsException();
		}

		if (!Authentication.checkPass(username, password)) {
			throw new InvalidCredentialsException();
		}

		Authentication.session.put(this.remoteAddress, username);

		return "Logged in as " + this.args.get(0);
	}

}
