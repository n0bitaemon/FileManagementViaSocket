package filemanager.com.test;
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
    
    public void send(String message) throws IOException {
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        System.out.println("Sent request to server: " + message);
        buffer.clear();
        int numBytes = socketChannel.read(buffer);
        String response = new String(buffer.array(), 0, numBytes).trim();
        System.out.println("Received response from server: " + response);
    }
    
    public void close() throws IOException {
        socketChannel.close();
    }
    
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 8080;
        Client client = new Client(host, port);
        Scanner sc = new Scanner(System.in);
        while(true) {
        	String input = sc.nextLine();
        	if(input.equals("exit")) {
        		break;
        	}
            client.send(input);
        }
        client.close();
        sc.close();
    }
}