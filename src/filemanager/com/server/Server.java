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

import filemanager.com.server.cmd.Command;
import filemanager.com.server.common.Environments;
import filemanager.com.server.exception.ServerException;

public class Server {
    private Selector selector;
    private ByteBuffer buffer;
    
    public Server(int port) throws IOException {
        selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        buffer = ByteBuffer.allocate(1024);
        System.out.println("Server started on port " + port);
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
                	if(Environments.DEBUG_MODE) {
                		e.printStackTrace();
                	}
                	System.out.println("[ERROR] Message sent from server is too large!");
                } catch (IOException e) {
                	if(Environments.DEBUG_MODE) {
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
        System.out.println("New client connected: " + socketChannel.getRemoteAddress());
    }
    
    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.clear();

        String remoteAddress = null;
        int numBytes = socketChannel.read(buffer);
        if (numBytes == -1) {
            disconnect(key);
            return;
        }

    	remoteAddress = socketChannel.getRemoteAddress().toString();
        
        String request = new String(buffer.array(), 0, numBytes, StandardCharsets.UTF_8).trim();
        
        System.out.println("Received request from " + remoteAddress + ": " + request);
        key.interestOps(SelectionKey.OP_WRITE);
        key.attach(request);
    }
    
    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        SocketAddress remoteAddress = socketChannel.getRemoteAddress();
        String req = (String) key.attachment();
        String res = getResponse(req, remoteAddress);
        
        buffer.clear();
        buffer.put(res.getBytes());
        buffer.flip();
        
        socketChannel.write(buffer);
        
        key.interestOps(SelectionKey.OP_READ);
        System.out.println("Sent response to " + remoteAddress + ": " + res);
    }
    
    private void disconnect(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        System.out.println("Client disconnected: " + socketChannel.getRemoteAddress());
        key.cancel();
        socketChannel.close();
    }
    
    private String getResponse(String req, SocketAddress remoteAddress) {
    	Command cmd = Command.parseCommandFromString(req, remoteAddress);
        if(cmd != null) {
        	// Only return validateResponse if there is an error in validation step
        	try {
        		// EXP00-J: Không bỏ qua giá trị trả về của hàm
        		if(!cmd.validate()) {
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
        }else {
        	return "Command not found!";
        }
    }
    
    public static void main(String[] args) {
        int port = 3000;
        Server server;
		try {
			server = new Server(port);
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			System.err.println("[ERROR] Cannot start server");
			return;
		}

        try {
			server.start();
		} catch (IOException e) {
			if(Environments.DEBUG_MODE) {
				e.printStackTrace();
			}
			System.err.println("[ERROR] Unexpected error!");
		}
    }
}