package it.unipi.ing.cds.main;
import java.awt.EventQueue;

import it.unipi.ing.cds.gui.ClientGUI;
import it.unipi.ing.cds.parameters.Parameters;

public class CDS {
	
	public static void main(String[] args) throws Exception {
		System.setProperty("java.rmi.server.hostname", Parameters.MYREGISTRY_CLIENT_HOST);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI frame = new ClientGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}