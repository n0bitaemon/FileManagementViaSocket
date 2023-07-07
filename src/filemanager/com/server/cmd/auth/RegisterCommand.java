package filemanager.com.server.cmd.auth;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.MinLengthException;
import filemanager.com.server.exception.ServerException;
import filemanager.com.server.exception.UserAlreadyExistException;
import filemanager.com.server.exception.UserAlreadyLoginException;
import filemanager.com.server.exception.UsernameStandard;

public class RegisterCommand extends Command {
	@Override
	public boolean validate() throws ServerException {

		if (!Validator.validateNumberOfArgs(getArgs(), 2)) {
			throw new InvalidNumberOfArgsException(2, getArgs().size());
		}	
		
		return true;
	}

	@Override
	public String exec() throws ServerException {
		if (Authentication.channelIsLoging(getRemoteAddress())) {
			throw new UserAlreadyLoginException(Authentication.session.get(getRemoteAddress()));
		}

		String username = Utils.normalizeString(this.getArgs().get(0));
		String password = this.getArgs().get(1);
		if (Authentication.findAccInDatabase(username)) {
			throw new UserAlreadyExistException(username);
		}
		
		if (!username.matches("[a-zA-Z0-9]+")) {
			throw new UsernameStandard();
		}
		
		if(password.length() <= 6) {
			throw new MinLengthException(password, 6);
		}

		Authentication.addAccountToDatabase(username, password);
		return String.format("Register account %s success", username);
	}

}
