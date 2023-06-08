package filemanager.com.server.cmd.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import filemanager.com.server.Response;
import filemanager.com.server.cmd.Command;
import filemanager.com.server.common.Constants;

public class FileDeleteCommand extends Command{
	// delete /folder/file.txt n0bita
	public static int NUMBER_OF_ARGS = 2;
	
	public Response validate() {
		System.out.println("[SERVER LOG] FILE DELETION VALIDATE");
		Response response = new Response();
		List<String> args = getArgs();
		
		// Checking for number of arguments
		if(args.size() != FileDeleteCommand.NUMBER_OF_ARGS) {
			response.setMessage("The command requires " + NUMBER_OF_ARGS + " arguments");
			return response;
		}
		
		String filePath = args.get(0);
		String user = args.get(1);
		Path path = Paths.get(Constants.STORAGE_DIR + "/" + user, filePath);
		System.out.println(path.toAbsolutePath());
		
		// Validate file path
		if(!Files.exists(path)) {
			response.setMessage("File not found");
			return response;
		}
		
		// Checking for credentials
		
		response.setStatus(true);
		return response;
	}

	public Response exec() {
		System.out.println("[SERVER LOG] FILE DELETION EXECUTION");
		return null;
	}
	
}
