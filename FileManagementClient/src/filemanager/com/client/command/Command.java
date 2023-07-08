package filemanager.com.client.command;

import java.util.ArrayList;
import java.util.List;

import filemanager.com.client.common.Utils;
import filemanager.com.client.exception.InvalidCommandException;

public class Command {
	private String name;
	private List<String> args;
	
	public String getName() {
		return this.name;
	}
	
	public List<String> getArgs() {
		return this.args;
	}
	
	public static Command parseCommandFromString(String msg) throws InvalidCommandException {
		String[] msgArr = Utils.translateCommandline(msg);
		if(msgArr == null) {
			throw new InvalidCommandException();
		}
		if(msgArr.length == 0) {
			throw new InvalidCommandException();
		}
		
		Command cmd = new Command();
		
		String cmdNameRaw = msgArr[0];
		String cmdNameOnlyAlphabet = Utils.removeNonAlphabetCharacter(cmdNameRaw);
		String cmdName = Utils.normalizeString(cmdNameOnlyAlphabet);
		cmd.name = cmdName;
		
		List<String> args = new ArrayList<>();
		for (int i = 1; i < msgArr.length; i++) {
			args.add(msgArr[i]);
		}
		cmd.args = args;
		
		return cmd;
	}
	
	public String toString() {
		if(args.isEmpty())
			return name;
		
		StringBuilder cmdStr = new StringBuilder(name);
		cmdStr.append(" ");
		cmdStr.append(String.join(" ", args));
		
		return cmdStr.toString();
	}
}
