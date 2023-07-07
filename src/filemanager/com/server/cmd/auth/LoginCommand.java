package filemanager.com.server.cmd.auth;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.exception.InvalidCredentialsException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.ServerException;
import filemanager.com.server.exception.UserAlreadyLoginException;

public class LoginCommand extends Command {
	private static final Logger LOGGER = LogManager.getLogger(LoginCommand.class);

	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", this.remoteAddress, Constants.AUTH_LOGIN_CMD);

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
		
		if (!username.matches("[a-zA-Z0-9]+")) { // no need since user can't create account with this kind of username 
			throw new InvalidCredentialsException();
		}

		return true;
	}

	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.AUTH_LOGIN_CMD);
		
		String username = this.args.get(0);
		String password = this.args.get(1);
		
		try {
			if (!Authentication.isUsernameInDb(username)) {
				throw new InvalidCredentialsException();
			}
		}catch (SQLException e) {
			throw new ServerException();
		}

		try {
			if (!Authentication.isValidPassword(username, password)) {
				throw new InvalidCredentialsException();
			}
		}catch (SQLException e) {
			throw new ServerException();
		}
		
		Authentication.session.put(this.remoteAddress, username);

		return "Logged in as " + this.args.get(0);
	}

}