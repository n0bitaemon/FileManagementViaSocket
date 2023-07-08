package filemanager.com.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import filemanager.com.server.cmd.Command;
import filemanager.com.server.common.Environments;
import filemanager.com.server.exception.InvalidCommandException;
import filemanager.com.server.exception.ServerException;

/**
 * The server accept and listen for incoming request from client
 * @author triet
 *
 */
public class Server implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger(Server.class);
	
	private Selector selector;
	private ByteBuffer buffer;

	private Server(int port) throws IOException {
		selector = Selector.open();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		buffer = ByteBuffer.allocate(1024);
		LOGGER.info("Server started on port {}", port);
	}

	private void start() throws IOException {
		while (true) {
			selector.select();

			Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();
				try {
					keyIterator.remove();
					if (!key.isValid()) {
						continue;
					}
					if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					} else if (key.isWritable()) {
						String request = (String) key.attachment();
						Response response = getResponse(request, key);
						if(response == null)
							continue;
						write(key, response);
					}
				} catch(Exception e) {
					if(Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					disconnect(key);
					continue;
				}
			}
		}
	}

	/**
	 * Accept a user connection
	 * 
	 * @param key
	 * @throws IOException
	 */
	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel;
		socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
		LOGGER.info("Connected: {}", socketChannel.getRemoteAddress());
	}

	/**
	 * Read user message from SelectionKey object
	 * 
	 * @param key
	 * @throws IOException
	 */
	public void read(SelectionKey key) throws IOException {
		if (key == null)
			return;

		SocketChannel socketChannel = (SocketChannel) key.channel();
		String remoteAddress = socketChannel.getRemoteAddress().toString();

		// read
		buffer.clear();
		int numBytes = socketChannel.read(buffer);
		if (numBytes == -1) {
			disconnect(key);
			return;
		}

		String request = new String(buffer.array(), 0, numBytes, StandardCharsets.UTF_8).trim();

		LOGGER.info("Request from {}: {}", remoteAddress, request);
		key.interestOps(SelectionKey.OP_WRITE);
		key.attach(request);
	}
	

	/**
	 * Write a message and send it to user
	 * 
	 * @param key
	 * @param response
	 * @throws IOException
	 */
	private void write(SelectionKey key, Response response) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		SocketAddress remoteAddress = socketChannel.getRemoteAddress();
		
		short status = response.getStatus() ? (short) 1 : (short) 0;
		String message = response.getMessage();
		
		// Request structure: status(short)|data(bytes)
		buffer.clear();
		buffer.putShort(status);
		buffer.put(message.getBytes());
		buffer.flip();

		socketChannel.write(buffer);

		key.interestOps(SelectionKey.OP_READ);
		LOGGER.info("Response to {}({}): {}", remoteAddress, status, message);
	}

	private void disconnect(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		LOGGER.info("DISCONNECTED: {}", socketChannel.getRemoteAddress());
		key.cancel();
		socketChannel.close();
	}

	/**
	 * This method get the user message as input, and then generate a
	 * response corresponding to that request.
	 * 
	 * @param req The message sent from user
	 * @param key SelectionKey object used to communicate with the user
	 * @return Response
	 */
	private Response getResponse(String req, SelectionKey key) {
		if(req.equals("exit")) {
			try {
				disconnect(key);
			} catch (IOException e) {
				if(Environments.DEBUG_MODE) {
					e.printStackTrace();
				}
			}
			return null;
		}
		SocketChannel socketChannel = (SocketChannel) key.channel();
		SocketAddress remoteAddress;
		try {
			remoteAddress = socketChannel.getRemoteAddress();
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			return new Response(false, "Error handling remote address");
		}
		
		Command cmd;
		try {
			cmd = Command.parseCommandFromString(req, remoteAddress, key);
		} catch (InvalidCommandException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			return new Response(false, "Invalid command");
		}
		if (cmd != null) {
			// Only return validateResponse if there is an error in validation step
			try {
				if (!cmd.validate()) {
					return new Response(false, "Unexpected error");
				}
			} catch (ServerException e) {
				return new Response(false, e.getMessage());
			}

			// Always return execResponse
			String execResponse;
			try {
				execResponse = cmd.exec();
			} catch (ServerException e) {
				return new Response(false, e.getMessage());
			}
			return new Response(true, execResponse);
		} else {
			return new Response(false, "Command not found");
		}
	}

	public static void main(String[] args) {
		int port = 3000;

		// ERR05-J: Using try-with-resource
		try (Server server = new Server(port)) {
			server.start();
		} catch (Exception e) {
			LOGGER.error("Unexpected error. Server stopped");
			if (Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() throws Exception {
		this.selector.close();
		LOGGER.info("Server is closed");
	}
	
	
}