package de.maikmerten.serialbootloader;

import de.maikmerten.serialbootloader.gui.MainWindow;
import javax.swing.SwingUtilities;

/**
 *
 * @author maik
 */
public class Main {
	
	
	public static void main(String[] args) throws Exception {
		
		String device = "/dev/ttyUSB0";
		if(args.length > 0) {
			device = args[0];
		}
		
		SerialConnection ser = new SerialConnection(device, 115200);
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
