package filemanager.com.server.cmd.auth;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.ServerException;
import filemanager.com.server.exception.UserAlreadyExistException;
import filemanager.com.server.exception.UserAlreadyLoginException;

public class RegisterCommand extends Command {
	@Override
	public boolean validate() throws ServerException {

		if (!Validator.validateNumberOfArgs(getArgs(), 2)) {
			throw new InvalidNumberOfArgsException(0, getArgs().size());
		}

		return true;
	}

	@Override
	public String exec() throws ServerException {
		if (Authentication.channelIsLoging(getRemoteAddress())) {
			throw new UserAlreadyLoginException(Authentication.session.get(getRemoteAddress()));
		}

		String username = this.getArgs().get(0);
		if (Authentication.findAccInDatabase(username)) {
			throw new UserAlreadyExistException(username);
		}

		Authentication.addAccountToDatabase(this.getArgs().get(0), this.getArgs().get(1));
		return String.format("Register account %s success", username);
	}

}
