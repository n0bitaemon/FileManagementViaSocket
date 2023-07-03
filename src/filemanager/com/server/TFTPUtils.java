package filemanager.com.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

public class TFTPUtils {
	public static final int BUFSIZE = 516;
	
	// TFTP Opcode
	public static final short OP_RRQ = 1;
	public static final short OP_WRQ = 2;
	public static final short OP_DAT = 3;
	public static final short OP_ACK = 4;
	public static final short OP_ERR = 5;
	
	// TFTP Error Code
	public static final short NOT_DEFINED = 0;
	public static final short FILE_NOT_FOUND = 1;
	public static final short ACCESS_VIOLATION = 2;
	public static final short DISK_FULL = 3;
	public static final short ILLEGAL_TFTP_OPERATION = 4;
	public static final short UNKNOWN_TRANSFER_ID = 5;
	public static final short FILE_ALREADY_EXISTS = 6;
	public static final short NO_SUCH_USER = 7;
	
	
	private TFTPUtils() {
	}
	
	public static boolean checkPacket(SocketChannel socketChannel, ByteBuffer buffer, int opcode) throws IOException {
		buffer.flip();
		int receivedOpcode = buffer.getShort();
		if(receivedOpcode != opcode) {
			return false;
		}
		return true;
	}
	
	public static void sendFileSize(Path source, SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
		long fileSize = Files.size(source);
		buffer.clear();
		// validate status = 1 means the validation step is successful
		buffer.putShort((short) 1); 
		buffer.putLong(fileSize);
		buffer.flip();
		socketChannel.write(buffer);
	}
	
	public static void sendDATPacket(SocketChannel socketChannel, ByteBuffer buffer, short blockNum, byte[] sendBuf, int length) throws IOException {
		buffer.clear();
		buffer.putShort(TFTPUtils.OP_DAT);
		buffer.putShort(blockNum);
		buffer.put(sendBuf, 0, length);
		buffer.flip();
		socketChannel.write(buffer);
	}
	
	public static boolean sendFile(File source, SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
		FileInputStream fis = new FileInputStream(source);
		short blockNum = 1;
		byte[] buf = new byte[TFTPUtils.BUFSIZE - 4];
		while(true) {
			int length;
			length = fis.read(buf);
			if(length == -1) {
				length = 0;
			}

			// Send DAT #blockNum packet
			sendDATPacket(socketChannel, buffer, blockNum, buf, length);
			
			// Receive ACK #blockNum
			buffer.clear();
			int numBytes;
			do {
				numBytes = socketChannel.read(buffer);
			}while(!(numBytes > 0));

			if(!checkPacket(socketChannel, buffer, OP_ACK)) {
				fis.close();
				return false;
			}
			
			// Check for the last packet
			if(length < 512) {
				fis.close();
				break;
			}
			
			// increase blockNum by 1
			blockNum += 1;
		}
		fis.close();
		return true;
	}
	
	
}
