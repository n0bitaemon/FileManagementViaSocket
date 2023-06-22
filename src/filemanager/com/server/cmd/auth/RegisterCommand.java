package filemanager.com.server.cmd.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Environments;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.MinLengthException;
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
		String password = this.getArgs().get(1);
		if (Authentication.findAccInDatabase(username)) {
			throw new UserAlreadyExistException(username);
		}

		if (password.length() <= 6) {
			throw new MinLengthException(password, 6);
		}

		Authentication.addAccountToDatabase(username, password);
		
		Path path = Paths.get(Constants.STORAGE_DIR + username);
		try {
			Files.createDirectories(path);
		} catch (IOException e) {
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ServerException();
		}
		
		return String.format("Register account %s success", username);
	}

}
