package filemanager.com.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    
    private ByteBuffer buffer;
    private SocketChannel socketChannel;
    
    public Client(String host, int port) throws IOException {
        buffer = ByteBuffer.allocate(1024);
        socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress(host, port));
        System.out.println("Connected to server at " + host + ":" + port);
    }
    
    public String send(String message) throws IOException {
    	// Send request
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        System.out.println(">>> " + message);
        buffer.clear();
        
        // Read response
        int numBytes = socketChannel.read(buffer);
        String response = new String(buffer.array(), 0, numBytes).trim();
        return response;
    }
    
    public void close() throws IOException {
        socketChannel.close();
    }
    
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 3000;
        Client client = new Client(host, port);
        Scanner sc = new Scanner(System.in);
        String cmd;
        String res = null;
        
        while(true) {
        	cmd = sc.nextLine();
        	res = client.send(cmd);
            System.out.println(res);
            if(res.equalsIgnoreCase("exit")) {
            	break;
            }
        }
        
        sc.close();
        
    }
}