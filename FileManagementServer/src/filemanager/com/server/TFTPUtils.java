package filemanager.com.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import filemanager.com.server.exception.ServerException;

/**
 * Provides methods to run TFTP protocol
 * @author n0bita-windows
 *
 */
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
	
	/**
	 * 
	 * Check the buffer is expected TFTP or not using the given opcode
	 * 
	 * @param socketChannel
	 * @param buffer The buffer that contains TFTP packet
	 * @param opcode The expected opcode of TFTP packet
	 * @return true, false
	 * @throws IOException
	 */
	public static boolean checkPacket(ByteBuffer buffer, int opcode) {
		buffer.flip();
		return buffer.getShort() == opcode;
	}
	
	/**
	 * @param source
	 * @param socketChannel
	 * @param buffer
	 * @throws IOException
	 */
	public static void sendFileSize(Path source, SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
		long fileSize = Files.size(source);
		buffer.clear();
		// validate status = 1 means the validation step is successful
		buffer.putShort((short) 1); 
		buffer.putLong(fileSize);
		buffer.flip();
		socketChannel.write(buffer);
	}
	
	/**
	 * Send a packet with status 1 (success)
	 * 
	 * @param socketChannel
	 * @param buffer
	 * @throws IOException
	 */
	public static void sendSuccessStatus(SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
		buffer.clear();
		buffer.putShort((short) 1);
		buffer.flip();
		socketChannel.write(buffer);
	}
	
	/**
	 * Send TFTP DAT packet
	 * 
	 * @param socketChannel
	 * @param buffer
	 * @param blockNum Block number of file
	 * @param sendBuf
	 * @param length The length of data
	 * @throws IOException
	 */
	public static void sendDATPacket(SocketChannel socketChannel, ByteBuffer buffer, short blockNum, byte[] sendBuf, int length) throws IOException {
		buffer.clear();
		buffer.putShort(TFTPUtils.OP_DAT);
		buffer.putShort(blockNum);
		buffer.put(sendBuf, 0, length);
		buffer.flip();
		socketChannel.write(buffer);
	}
	
	/**
	 * Send TFTP ACK packet
	 * 
	 * @param socketChannel
	 * @param buffer
	 * @param blockNum Block number of file
	 * @throws IOException
	 */
	public static void sendACKPacket(SocketChannel socketChannel, ByteBuffer buffer, short blockNum) throws IOException {
		buffer.clear();
		buffer.putShort(TFTPUtils.OP_ACK);
		buffer.putShort(blockNum);
		buffer.flip();
		socketChannel.write(buffer);
	}
	
	/**
	 * Send TFTP RRQ packet
	 * 
	 * @param source The destination path
	 * @param socketChannel
	 * @param buffer
	 * @throws IOException
	 */
	public static void sendRRQPacket(String source, SocketChannel socketChannel, ByteBuffer buffer) throws IOException {
		buffer.clear();
		buffer.putShort(OP_RRQ);
		buffer.put(source.getBytes());
		buffer.put((byte) 0);
		buffer.put("octet".getBytes());
		buffer.put((byte) 0);
		buffer.flip();
		socketChannel.write(buffer);
	}
	
	/**
	 * Start sending file block by block, and wait for coming ACK packet
	 * @param source
	 * @param socketChannel
	 * @param buffer
	 * @return true if file is sent successfully, false otherwise
	 * @throws ServerException
	 */
	public static boolean sendFile(File source, SocketChannel socketChannel, ByteBuffer buffer) throws ServerException {
		try(FileInputStream fis = new FileInputStream(source)){
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
				}while(numBytes <= 0);

				if(!checkPacket(buffer, OP_ACK)) {
					return false;
				}
				
				// Check for the last packet
				if(length < 512) {
					break;
				}
				
				// increase blockNum by 1
				blockNum += 1;
			}
			return true;
		}catch (IOException e) {
			throw new ServerException();
		}
		
	}
	
	/**
	 * Read file from client block by block, and send ACK packet with each received block
	 * @param dest The location of uploaded file
	 * @param socketChannel
	 * @param buffer
	 * @return
	 * @throws ServerException
	 */
	public static boolean receiveFile(Path dest, SocketChannel socketChannel, ByteBuffer buffer) throws ServerException {
		short blockNum = 1;
		short opcode;
		try(FileOutputStream fos = new FileOutputStream(dest.toFile())){
			while(true) {
				// Receive DAT packet
				buffer.clear();
				
				int numBytes;
				do {
					numBytes = socketChannel.read(buffer);
				}while(numBytes <= 0);
				
				buffer.flip();
				opcode = buffer.getShort();
				if(opcode != TFTPUtils.OP_DAT) {
					return false;
				}
				
				// Check for synchronization
				short blockNumInPacket = buffer.getShort();
				if(blockNumInPacket != blockNum) {
					return false;
				}
				
				// Write data to buffer
				byte[] dataInBytes = new byte[buffer.remaining()];
				buffer.get(dataInBytes);
				fos.write(dataInBytes);
				
				// send ACK block
				sendACKPacket(socketChannel, buffer, blockNumInPacket);
				
				// check for the last block
				if(dataInBytes.length < 512) {
					break;
				}
				blockNum++;
			}
			
			return true;
		} catch (IOException e) {
			throw new ServerException();
		}
		
	}
	
	
}
