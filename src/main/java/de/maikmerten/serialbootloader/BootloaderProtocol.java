package de.maikmerten.serialbootloader;

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
			Thread.sleep(1);
			long delta = System.currentTimeMillis() - starttime;
			if(delta > 500) {
				throw new Exception("timeout during byte read");
			}
		}
		
		return (byte)(is.read() & 0xFF);
	}
	
	private void byteOut(byte b) throws Exception {
		OutputStream os = conn.getOutputStream();
		byte[] buf = {b};
		os.write(buf);
	}
	
	private void cmdOut(byte[] command) throws Exception {
		for(int i = 0; i < command.length; ++i) {
			byte out = command[i];
			byteOut(out);
			byte in = byteIn();
			
			if(out != in) {
				throw new Exception("Command was not mirrored correctly");
			}
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
	
	public void writeByte(byte b) throws Exception {
		byte op = 'W';
		byte[] cmd = {op, b};
		cmdOut(cmd);
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
		
		for(byte b : data) {
			writeByte(b);
		}
	}
	
	public void callAddress(long address) throws Exception {
		writeAddress(address);
		call();
	}
	
	
	
}
