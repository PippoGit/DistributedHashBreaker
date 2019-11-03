package it.unipi.ing.cds.main;
import java.awt.EventQueue;

import it.unipi.ing.cds.gui.ClientGUI;

public class CDS {
	
	public static void main(String[] args) throws Exception {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientGUI frame = ClientGUI.getInstance();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}