package filemanager.com.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
	private static SocketChannel client;
    private static ByteBuffer buffer;
    private static Client instance;

    public static Client start() {
        if (instance == null)
            instance = new Client();

        return instance;
    }

    public static void stop() throws IOException {
        client.close();
        buffer = null;
    }
    
    private Client() {
        try {
            client = SocketChannel.open(new InetSocketAddress("localhost", 3000));
            buffer = ByteBuffer.allocate(256);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String sendMessage(String msg) {
        buffer = ByteBuffer.wrap(msg.getBytes());
        String response = null;
        try {
            client.write(buffer);
            buffer.clear();
            client.read(buffer);
            response = new String(buffer.array()).trim();
            System.out.println("response=" + response);
            buffer.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
    
    public static void main(String[] args) {
		Client client = Client.start();
		Scanner sc = new Scanner(System.in);
		
		while(true) {
			String s = sc.nextLine();
			if(s.equalsIgnoreCase("exit"))
				break;
			client.sendMessage(s);
		}
		
		sc.close();
	}
}
