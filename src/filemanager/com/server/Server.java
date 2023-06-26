package filemanager.com.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
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
import filemanager.com.server.exception.ServerException;

public class Server implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger(Server.class);
	
	private Selector selector;
	private ByteBuffer buffer;

	public Server(int port) throws IOException {
		selector = Selector.open();
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.bind(new InetSocketAddress(port));
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		buffer = ByteBuffer.allocate(1024);
		LOGGER.info("Server started on port {}", port);
	}

	public void start() throws IOException {
		while (true) {
			selector.select();

			Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();
				keyIterator.remove();
				if (!key.isValid()) {
					continue;
				}
				try {
					if (key.isAcceptable()) {
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					} else if (key.isWritable()) {
						write(key);
					}
				} catch (BufferOverflowException e) {
					if (Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					LOGGER.error("Message too long");
				} catch (IOException e) {
					if (Environments.DEBUG_MODE) {
						e.printStackTrace();
					}
					disconnect(key);
				}
			}
		}
	}

	private void accept(SelectionKey key) throws IOException {
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
		SocketChannel socketChannel;
		socketChannel = serverSocketChannel.accept();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ);
		LOGGER.info("Connected: {}", socketChannel.getRemoteAddress());
	}

	private void read(SelectionKey key) throws IOException {
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

	private void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		SocketAddress remoteAddress = socketChannel.getRemoteAddress();
		String req = (String) key.attachment();
		String res = getResponse(req, remoteAddress, socketChannel);

		buffer.clear();
		buffer.put(res.getBytes());
		buffer.flip();

		socketChannel.write(buffer);

		key.interestOps(SelectionKey.OP_READ);
		LOGGER.info("Response to {}: {}", remoteAddress, res);
	}

	private void disconnect(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		LOGGER.info("DISCONNECTED: {}", socketChannel.getRemoteAddress());
		key.cancel();
		socketChannel.close();
	}

	private String getResponse(String req, SocketAddress remoteAddress, SocketChannel socketChannel) {
		Command cmd = Command.parseCommandFromString(req, remoteAddress, socketChannel);
		if (cmd != null) {
			// Only return validateResponse if there is an error in validation step
			try {
				// EXP00-J: Không bỏ qua giá trị trả về của hàm
				if (!cmd.validate()) {
					return "Validation error";
				}
			} catch (ServerException e) {
				return e.getMessage();
			}

			// Always return execResponse
			String execResponse;
			try {
				execResponse = cmd.exec();
			} catch (ServerException e) {
				return e.getMessage();
			}
			return execResponse;
		} else {
			return "Command not found!";
		}
	}

	public static void main(String[] args) {
		int port = 3000;

		// ERR05-J: Using try-with-resource
		try (Server server = new Server(port)) {
			server.start();
		} catch (Exception e) {
			LOGGER.error("Unexpected error while starting server");
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