package filemanager.com.server.cmd.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.AuthCommand;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.common.Constants;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.NotLoggedInException;
import filemanager.com.server.exception.ServerException;

public class LogoutCommand extends AuthCommand {
	private static final Logger LOGGER = LogManager.getLogger(LogoutCommand.class);

	@Override
	public boolean validate() throws ServerException {
		LOGGER.info("{}: validate command - {}", this.remoteAddress, Constants.AUTH_LOGOUT_CMD);
		
		// check if this channal has loged in any account 
		if(!isAuthenticated()) {
			throw new NotLoggedInException();
		}
		
		if (!Validator.validateNumberOfArgs(this.args, 0)) {
			throw new InvalidNumberOfArgsException(0, this.args.size());
		}
		return true;
	}

	@Override
	public String exec() throws ServerException {
		LOGGER.info("{}: exec command - {}", this.remoteAddress, Constants.AUTH_LOGIN_CMD);
		
		//remove the current address from session
		Authentication.session.remove(this.remoteAddress);
		return "Logged out";
	}

}
