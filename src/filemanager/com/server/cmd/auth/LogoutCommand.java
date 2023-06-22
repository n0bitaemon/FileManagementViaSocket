package filemanager.com.server.cmd.auth;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.NotLoggedInException;
import filemanager.com.server.exception.ServerException;

public class LogoutCommand extends Command {

	@Override
	public boolean validate() throws ServerException {
		if (!Validator.validateNumberOfArgs(getArgs(), 0)) {
			throw new InvalidNumberOfArgsException(0, getArgs().size());
		}

		if (!Authentication.channelIsLoging(getRemoteAddress())) {
			throw new NotLoggedInException();
		}
		return true;
	}

	@Override
	public String exec() throws ServerException {
		Authentication.session.remove(getRemoteAddress());
		return "Logged out";
	}

}
