package filemanager.com.client;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.client.command.Command;
import filemanager.com.client.common.Constants;
import filemanager.com.client.common.Environments;
import filemanager.com.client.common.Utils;
import filemanager.com.client.exception.ClientException;
import filemanager.com.client.exception.InvalidCommandException;

/**
 * The Client is used to communicate with the Server
 * 
 * @author triet
 *
 */
public class Client implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger(Client.class);
	
	private ByteBuffer buffer;
	private SocketChannel socketChannel;

	private Client(String host, int port) throws IOException {
		buffer = ByteBuffer.allocate(1024);
		socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress(host, port));
		
		LOGGER.info("Connected to server at {}: {}", host, port);
	}

	public void send(String message) throws IOException {
		// Send request
		buffer.clear();
		try {
			buffer.put(message.getBytes());
		} catch (BufferOverflowException e) {
			throw new BufferOverflowException();
		}
		buffer.flip();
		socketChannel.write(buffer);
	}
	
	public Response receive() throws IOException {
		buffer.clear();
		int numBytes = socketChannel.read(buffer);
		if(numBytes == -1) {
			return new Response(false, Constants.ERR_NO_RESPONSE);
		}
		
		buffer.flip();
		int status = buffer.getShort();
		String message = new String(buffer.array(), 1, numBytes-1, StandardCharsets.UTF_8).trim();
		
		return new Response(status != 0, message);
	}

	public void disconnect() throws IOException {
		socketChannel.close();
		LOGGER.info(Constants.MSG_DISCONNECTED);
	}

	public void loop() {
		Scanner sc = new Scanner(System.in);
		String cmdStr;
		Response response = new Response();

		while (true) {
			response.setResponse(false, "");
			cmdStr = sc.nextLine();
			System.out.println(">>> " + cmdStr);

			if(cmdStr.equals("exit")) {
				try {
					disconnect();
					System.out.println("Client shut down");
					break;
				} catch (IOException e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					response.setResponse(false, "Cannot disconnect to server");
				}
			}
			
			if(cmdStr.equals("help")) {
				Utils.showHelpMenu();
				continue;
			}
			
			Command cmd;
			try {
				cmd = Command.parseCommandFromString(cmdStr);
			} catch (InvalidCommandException e) {
				if(Environments.DEBUG_MODE) {
					e.printStackTrace();
				}
				response.setResponse(false, e.getMessage());
				System.out.println(response);
				continue;
			}
			
			if(cmd.getName().equals("download")) {
				// Client validation
				// Check for number of arguments
				if(cmd.getArgs().size() != 2) {
					response.setResponse(false, "Invalid download command, expected 2 arguments");
					System.out.println(response);
					continue;
				}
				String destRaw = cmd.getArgs().get(1);
				// Check if file exist
				Path dest = Paths.get(destRaw);
				if(Files.exists(dest)) {
					response.setResponse(false, "File is already exist");
					System.out.println(response);
					continue;
				}
				
				// Create file
				try {
					Files.createFile(dest);
				} catch (IOException e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					response.setResponse(false, "Cannot access destination path");
					System.out.println(response);
					continue;
				}
				
				// Send request
				try {
					send(cmd.toString());
					response = receive();
				} catch (NotYetConnectedException e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					response.setResponse(false, Constants.MSG_DISCONNECTED);
				} catch (BufferOverflowException e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					response.setResponse(false, Constants.ERR_MESSAGE_TO_LONG);
				} catch (IOException e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					response.setResponse(false, Constants.ERR_CANNOT_REACH_SERVER);
				}
				
				// If server validation success, continue sending packet
				if(response.getStatus()) {
					String sourceRaw = cmd.getArgs().get(0);
					try {
						response = download(sourceRaw, dest);
					} catch (ClientException e) {
						if(Environments.DEBUG_MODE) {
							e.printStackTrace();
						}
						System.out.println("Error while downloading file");
						continue;
					}
				}else if(cmd.getName().equals("upload")) {
					response.setResponse(true, "Uploaded");
				}else {
					// Delete if fail validation
					try {
						Files.deleteIfExists(dest);
					} catch (IOException e) {
						System.out.println("Cannot delete file");
					}
				}
				
			}else if(cmd.getName().equals("upload")) {
				// Client validation
				if(cmd.getArgs().size() != 2) {
					response.setResponse(false, "Invalid upload command, expected 2 arguments");
					System.out.println(response);
					continue;
				}
				String sourceRaw = cmd.getArgs().get(0);
				// Check if file exist
				Path source = Paths.get(sourceRaw);
				if(!Files.exists(source)) {
					response.setResponse(false, "File not exist");
					System.out.println(response);
					continue;
				}
				if(!Files.isReadable(source)) {
					response.setResponse(false, "Cannot read file");
					System.out.println(response);
					continue;
				}
				
				// Send request and get validation response
				try {
					send(cmd.toString());
					response = receive();
				} catch (NotYetConnectedException e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					response.setResponse(false, Constants.MSG_DISCONNECTED);
				} catch (BufferOverflowException e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					response.setResponse(false, Constants.ERR_MESSAGE_TO_LONG);
				} catch (IOException e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					response.setResponse(false, Constants.ERR_CANNOT_REACH_SERVER);
				}
				
				
				// If the validation step success, start uploading
				if(response.getStatus()) {
					try {
						response = upload(source);
					} catch (ClientException e) {
						if(Environments.DEBUG_MODE) {
							e.printStackTrace();
						}
						System.out.println("Error while uploading file");
						continue;
					}
				}
				
			}else {
				try {
					send(cmd.toString());
					response = receive();
				} catch (NotYetConnectedException e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					response.setResponse(false, Constants.MSG_DISCONNECTED);
				} catch (BufferOverflowException e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					response.setResponse(false, Constants.ERR_MESSAGE_TO_LONG);
				} catch (IOException e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					response.setResponse(false, Constants.ERR_CANNOT_REACH_SERVER);
				}
			}
			
			System.out.println(response);
			
		}

		sc.close();
	}
	
	public Response upload(Path source) throws ClientException {
		Response response = new Response();
		ByteBuffer tftpBuffer = ByteBuffer.allocate(TFTPUtils.BUFSIZE);
		
		try {
			int numBytes;
			// Receive RRQ packet
			tftpBuffer.clear();
			do {
				numBytes = socketChannel.read(tftpBuffer);
			} while(numBytes <= 0);
			
			if(!TFTPUtils.checkPacket(tftpBuffer, TFTPUtils.OP_RRQ)) {
				throw new ClientException();
			}
			
			// Start sending file
			if(!TFTPUtils.sendFile(source.toFile(), socketChannel, tftpBuffer)) {
				throw new ClientException();
			}
			
			// Read last response
			response = receive();
			
			return response;
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ClientException();
		}
	}
	
	public Response download(String sourceRaw, Path dest) throws ClientException {
		Response response;
		
		ByteBuffer tftpBuffer = ByteBuffer.allocate(TFTPUtils.BUFSIZE);
		
		try {
			// Send RRQ packet
			TFTPUtils.sendRRQPacket(sourceRaw, socketChannel, tftpBuffer);
			
			
			// Receive file
			TFTPUtils.receiveFile(dest, socketChannel, tftpBuffer);
			
			// Read final response
			response = receive();
			
			return response;
		} catch (Exception e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			throw new ClientException();
		}
	}

	public static void main(String[] args) {
		String host = "localhost";
		int port = 3000;

		try (Client client = new Client(host, port)) {
			System.out.println("Connected to server");
			client.loop();
			client.disconnect();
		} catch (Exception e) {
			LOGGER.error("Cannot connect to server");
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws Exception {
		socketChannel.close();
	}
	
	public static void printBuffer(ByteBuffer buffer) {
		System.out.printf("Position: %d, Limit: %d\n", buffer.position(), buffer.limit());
	}
}