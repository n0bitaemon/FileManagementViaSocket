package filemanager.com.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client implements AutoCloseable {
	private ByteBuffer buffer;
	private SocketChannel socketChannel;

	public Client(String host, int port) throws IOException {
		buffer = ByteBuffer.allocate(1024);
		socketChannel = SocketChannel.open();
		socketChannel.connect(new InetSocketAddress(host, port));
		System.out.println("Connected to server at " + host + ":" + port);
	}

	public String send(String message) throws IOException, NotYetConnectedException {
		// Send request
		buffer.clear();
		try {
			buffer.put(message.getBytes());
		} catch (BufferOverflowException e) {
			throw new BufferOverflowException();
		}
		buffer.flip();
		socketChannel.write(buffer);
		System.out.println(">>> " + message);

		// Read response
		buffer.clear();
		System.out.println("previous");
		int numBytes = socketChannel.read(buffer);
		System.out.println("after");
		return new String(buffer.array(), 0, numBytes, StandardCharsets.UTF_8).trim();
	}

	public void disconnect() throws IOException {
		socketChannel.close();
		System.out.println("Disconnected to server!");
	}

	public void loop() throws IOException {
		Scanner sc = new Scanner(System.in);
		String cmd;
		String res;

		while (true) {
			cmd = sc.nextLine();
			if (cmd.equalsIgnoreCase("exit")) {
				break;
			}
			try {
				res = send(cmd);
			} catch (NotYetConnectedException e) {
				res = "The server is disconnected!";
			} catch (BufferOverflowException e) {
				res = "Client error: Message to long!";
			} catch (IOException e) {
				res = "Unexpected error!";
			}
			System.out.println(res);
		}

		sc.close();
	}

	public static void main(String[] args) {
		String host = "localhost";
		int port = 3000;

		try (Client client = new Client(host, port)) {
			client.loop();
			client.disconnect();
		} catch (Exception e) {
			System.out.println("[ERROR] CANNOT CONNECT TO SERVER");
			e.printStackTrace();
		}

	}

	@Override
	public void close() throws Exception {
		socketChannel.close();
	}
}