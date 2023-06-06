package filemanager.com.server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server{
	private static final String POISON_PILL = "POISON_PILL";
	
	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		ServerSocketChannel serverSocket = ServerSocketChannel.open();
		serverSocket.bind(new InetSocketAddress("localhost", 3000));
		serverSocket.configureBlocking(false);
		serverSocket.register(selector, SelectionKey.OP_ACCEPT);
		ByteBuffer buffer = ByteBuffer.allocate(256);
		
		while(true) {
			selector.select();
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
			Iterator<SelectionKey> iter = selectedKeys.iterator();
			while(iter.hasNext()) {
				SelectionKey key = iter.next();
				System.out.println(key.toString());
				if(key.isAcceptable()) {
					register(selector, serverSocket);
				}
				
				if(key.isReadable()) {
					answerWithEcho(buffer, key);
				}
				iter.remove();
			}
		}
	}
	
	private static void answerWithEcho(ByteBuffer buffer, SelectionKey key)
		      throws IOException {
	    SocketChannel client = (SocketChannel) key.channel();
	    String request = null;
	    int r = client.read(buffer);
	    if (r == -1 || new String(buffer.array()).trim().equals(POISON_PILL)) {
	        client.close();
	        System.out.println("Not accepting client messages anymore");
	    }else {
	        buffer.flip();
	        client.write(buffer);
	        buffer.clear();
	        request = new String(buffer.array()).trim();
	        System.out.println("Request: " + request);
	    }
	}
	
	private static void register(Selector selector, ServerSocketChannel serverSocket)
		      throws IOException {
        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
	}
	
	public static Process start() throws IOException {
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = Server.class.getCanonicalName();
		
		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
		
		return builder.start();
	}
}