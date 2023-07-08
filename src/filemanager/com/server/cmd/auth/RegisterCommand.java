package filemanager.com.server.cmd.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.common.Environments;
import filemanager.com.server.common.Utils;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.InvalidUsernameException;
import filemanager.com.server.exception.MinLengthException;
import filemanager.com.server.exception.ServerException;
import filemanager.com.server.exception.UserAlreadyExistException;
import filemanager.com.server.exception.UserAlreadyLoginException;

public class RegisterCommand extends Command {
	private static final Logger LOGGER = LogManager.getLogger(LoginCommand.class);
	
	@Override
	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", this.remoteAddress, Constants.AUTH_REGISTER_CMD);
		
		if (!Validator.validateNumberOfArgs(this.args, 2)) {
			throw new InvalidNumberOfArgsException(2, this.args.size());
		}
		
		this.args.set(0, Utils.normalizeString(this.args.get(0)));

		//user must log out before they can create another account
		if (Authentication.channelIsLoging(this.remoteAddress)) {
			throw new UserAlreadyLoginException(Authentication.session.get(this.remoteAddress));
		}

		String username = this.args.get(0);
		String password = this.args.get(1);
		
		//check if this username has been already used
		try {
			if (Authentication.isUsernameInDb(username)) {
				throw new UserAlreadyExistException(username);
			}
		}catch (SQLException e) {
			throw new ServerException();
		}
		
		if (!username.matches("[a-zA-Z0-9]+")) {
			throw new InvalidUsernameException();
		}
		
		//password must has lenght longer than 6
		if(password.length() <= 6) {
			throw new MinLengthException("password", 6);
		}
		
		return true;
	}

	@Override
	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.AUTH_REGISTER_CMD);
		
		String username = this.args.get(0);
		String password = this.args.get(1);
		
		//insert new account's infor into database
		try {
			Authentication.addAccountToDatabase(username, password);
		} catch (SQLException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ServerException();
		}
		
		//create a new directory whenever a new account is created
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
