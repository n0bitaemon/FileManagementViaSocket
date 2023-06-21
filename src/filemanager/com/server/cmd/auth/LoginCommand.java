package filemanager.com.server.cmd.auth;

import java.util.Enumeration;

import filemanager.com.server.auth.Authentication;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.cmd.validate.Validator;
import filemanager.com.server.exception.InvalidCredentialsException;
import filemanager.com.server.exception.InvalidNumberOfArgsException;
import filemanager.com.server.exception.ServerException;
import filemanager.com.server.exception.UserAlreadyLoginException;

public class LoginCommand extends Command{
	private String username;
	private String password;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean validate() throws ServerException {
		//System.out.println("Login validate()");
		//Checking for number of arguments
		
		
		if(!Validator.validateNumberOfArgs(getArgs(), 2)) {
			throw new InvalidNumberOfArgsException(2, getArgs().size());
		}
		
		//Checking for credentials
		
		if(Authentication.channelIsLoging(getRemoteAddress())) {
			throw new UserAlreadyLoginException(Authentication.session.get(getRemoteAddress()));
		}
		
		if(Authentication.accIsLoging(this.getArgs().get(0))) {
			Authentication.session.remove(Authentication.accOfChannel(this.getArgs().get(0)));
		}
		
		return true;
	}

	public String exec() throws ServerException {
		//System.out.println("Login exec()");
		if (!Authentication.findAccInDatabase(getUsername())) {
			throw new InvalidCredentialsException();
		} 
		
		if (!Authentication.checkPass(this.getArgs().get(0), this.getArgs().get(1))) {
			throw new InvalidCredentialsException();
		}
		
		Authentication.session.put(getRemoteAddress(), getUsername());
		
		return "Logged in as " + this.getArgs().get(0);
	}


}
