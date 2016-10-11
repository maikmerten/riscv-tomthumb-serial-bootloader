package de.maikmerten.serialbootloader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author maik
 */
public class BootloaderProtocol {
	
	private final SerialConnection conn;
	
	public BootloaderProtocol(SerialConnection conn) {
		this.conn = conn;
	}
	
	private byte byteIn() throws Exception {
		InputStream is = conn.getInputStream();
		
		long starttime = System.currentTimeMillis();
		while(is.available() < 1) {
			//Thread.sleep(1);
			long delta = System.currentTimeMillis() - starttime;
			if(delta > 2000) {
				throw new Exception("timeout during byte read");
			}
		}
		
		return (byte)(is.read() & 0xFF);
	}
	
	private void bytesOut(byte[] bytes) throws Exception {
		OutputStream os = conn.getOutputStream();
		os.write(bytes);
	}
	
	private void cmdOut(byte[] command) throws Exception {
		int checksum = 0;
		for(int i = 0; i < command.length; ++i) {
			checksum ^= (command[i] & 0xFF);
		}
		bytesOut(command);
		
		int in = byteIn() & 0xFF;
			
		if(checksum != in) {
			throw new Exception("Invalid checksum received, expected " + checksum + " but received " + in);
		}
	}
	
	public void writeAddress(long address) throws Exception {
		byte op = 'A';
		byte a0 = (byte)((address >> 24) & 0xFF);
		byte a1 = (byte)((address >> 16) & 0xFF);
		byte a2 = (byte)((address >> 8) & 0xFF);
		byte a3 = (byte)(address & 0xFF);
		
		byte[] cmd = {op, a0, a1, a2, a3};
		cmdOut(cmd);
	}
	
	public byte readByte() throws Exception {
		byte op = 'R';
		byte[] cmd = {op};
		cmdOut(cmd);
		return byteIn();
	}
	
	public void writeBytes(byte[] data) throws Exception {
		byte op = 'W';
		
		int len = data.length;
		byte l0 = (byte)((len >> 24) & 0xFF);
		byte l1 = (byte)((len >> 16) & 0xFF);
		byte l2 = (byte)((len >> 8) & 0xFF);
		byte l3 = (byte)(len & 0xFF);
		byte[] head = {op, l0, l1, l2, l3};
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(head.length + data.length);
		baos.write(head);
		baos.write(data);

		cmdOut(baos.toByteArray());
	}
	
	
	public void call() throws Exception {
		byte op = 'C';
		byte[] cmd = {op};
		cmdOut(cmd);
	}
	
	
	public byte[] readMemArea(long address, int bytes) throws Exception {
		byte[] result = new byte[bytes];
		
		writeAddress(address);
		for(int i = 0; i < result.length; ++i) {
			result[i] = readByte();
		}
	
		return result;
	}
	
	public void writeMemArea(long address, byte[] data) throws Exception {
		writeAddress(address);
		writeBytes(data);
	}
	
	public void callAddress(long address) throws Exception {
		writeAddress(address);
		call();
	}
	
	
	
}
