package it.unipi.ing.cds.main;
import java.awt.EventQueue;

import it.unipi.ing.cds.gui.ClientGUI;

public class CDS {
	
	public static void main(String[] args) throws Exception {
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