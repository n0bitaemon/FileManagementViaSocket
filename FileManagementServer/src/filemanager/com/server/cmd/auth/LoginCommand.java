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

/**
 * Authenticate user <br>
 * Syntax: login {@literal <}username> {@literal <}password><br>
 * Arguments: <br>
 * - username: User's username <br>
 * - password: User's password
 * @author cuong
 *
 */
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
		
		//check if the account that user loged in is being used in any others address. if true, log out this account from found address
		String username = this.args.get(0);
		if (Authentication.accIsLoging(username)) {
			Authentication.session.remove(Authentication.accOfChannel(username));
		}
		
		//check if user input an username contains any non-alphanumeric character
		if (!username.matches("[a-zA-Z0-9]+")) { 
			throw new InvalidCredentialsException();
		}

		return true;
	}

	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.AUTH_LOGIN_CMD);
		
		String username = this.args.get(0);
		String password = this.args.get(1);
		
		try {
			//check if account exist
			if (!Authentication.isUsernameInDb(username)) {
				throw new InvalidCredentialsException();
			}
		}catch (SQLException e) {
			throw new ServerException();
		}

		try {
			//check if the input password was right
			if (!Authentication.isValidPassword(username, password)) {
				throw new InvalidCredentialsException();
			}
		}catch (SQLException e) {
			throw new ServerException();
		}
		
		//insert a pair value: address, username; express that this account is being used in this address
		Authentication.session.put(this.remoteAddress, username);

		return "Logged in as " + this.args.get(0);
	}

}