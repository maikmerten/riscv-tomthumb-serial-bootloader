package de.maikmerten.serialbootloader;

import de.maikmerten.serialbootloader.gui.MainWindow;
import javax.swing.SwingUtilities;

/**
 *
 * @author maik
 */
public class Main {
	
	
	public static void main(String[] args) throws Exception {
		
		SerialConnection ser = new SerialConnection("/dev/ttyUSB0", 9600);
		BootloaderProtocol prot = new BootloaderProtocol(ser);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainWindow main = new MainWindow(prot);
				main.setVisible(true);
			}
		});
		
	}
	
}
