package filemanager.com.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import filemanager.com.server.cmd.Command;

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
                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                } else if (key.isWritable()) {
                    write(key);
                }
            }
        }
    }
    
    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("New client connected: " + socketChannel.getRemoteAddress());
    }
    
    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        buffer.clear();
        int numBytes = socketChannel.read(buffer);
        if (numBytes == -1) {
            disconnect(key);
            return;
        }
        String request = new String(buffer.array(), 0, numBytes).trim();
        System.out.println("Received request from " + socketChannel.getRemoteAddress() + ": " + request);
        key.interestOps(SelectionKey.OP_WRITE);
        key.attach(request);
    }
    
    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        String req = (String) key.attachment();
        String res = getResponse(req);
        
        buffer.clear();
        buffer.put(res.getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        
        key.interestOps(SelectionKey.OP_READ);
        System.out.println("Sent response to " + socketChannel.getRemoteAddress() + ": " + res);
    }
    
    private void disconnect(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        System.out.println("Client disconnected: " + socketChannel.getRemoteAddress());
        key.cancel();
        socketChannel.close();
    }
    
    private String getResponse(String req) {
    	Command cmd = Command.parseCommandFromString(req);
    	Response res = new Response();
        if(cmd != null) {
        	try {
        		res = cmd.validate();
        		if(!res.isStatus()) {
        			return res.getMessage();
        		}
        		res = cmd.exec();
        		if(!res.isStatus()) {
        			return res.getMessage();
        		}
        	}catch (Exception e) {
        		e.printStackTrace();
        		return "Unexpected error!";
			}
        }else {
        	return "Command not found!";
        }
        
		return "Command is executed!";
    }
    
    public static void main(String[] args) throws IOException {
        int port = 3000;
        Server server = new Server(port);
        server.start();
    }
}