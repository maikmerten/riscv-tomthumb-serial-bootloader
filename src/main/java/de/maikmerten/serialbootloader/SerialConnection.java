package de.maikmerten.serialbootloader;

import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author maik
 */
public class SerialConnection {
	
	private final SerialPort port;
	private final InputStream input;
	private final OutputStream output;
	
	public SerialConnection(String portname, int baud) throws Exception {
		SerialPort serport = SerialPort.getCommPort(portname);
		serport.openPort();
		serport.setBaudRate(baud);
		serport.setNumDataBits(8);
		serport.setNumStopBits(1);
		serport.setParity(0);
		
		
		this.port = serport;
		this.input = serport.getInputStream();
		this.output = serport.getOutputStream();
	}
	

	
	public InputStream getInputStream() {
		return input;
	}
	
	public OutputStream getOutputStream() {
		return output;
	}
	
	public void close() {
		try {
			input.close();
		} catch (IOException ex) {
			Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, "could not close input stream", ex);
		}
		try {
			output.close();
		} catch (IOException ex) {
			Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, "could not close output stream", ex);
		}
		
	}
	
	
}
