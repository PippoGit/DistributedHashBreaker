package it.unipi.ing.cds.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import it.unipi.ing.cds.worker.Worker;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ClientGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	private JPanel contentPane;
	private JButton startBtn;
	private JTextArea textLog;
	private JScrollPane scrollPane;

	public ClientGUI() {
		setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 453, 760);
		contentPane = new JPanel();
		contentPane.setBackground(Color.GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		startBtn = new JButton("START");
		startBtn.setFont(new Font("Consolas", Font.PLAIN, 12));
		startBtn.setBackground(SystemColor.textHighlight);
		startBtn.setForeground(SystemColor.inactiveCaptionBorder);
		startBtn.setBounds(164, 23, 89, 35);
		startBtn.setFocusPainted(false);
		contentPane.add(startBtn);
		
		textLog = new JTextArea();
		textLog.setEditable(false);
		textLog.setVisible(false);
		textLog.setBounds(10, 72, 417, 393);
		
		
		scrollPane = new JScrollPane(textLog);
		scrollPane.setBounds(10, 69, 417, 265);
		contentPane.add(scrollPane);
		
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				startBtn.setEnabled(false);
				textLog.setVisible(true);
				contentPane.revalidate();
				
				// START THE JOB
				new Worker(ClientGUI.this).start();
				
			}
		});
	}
	public synchronized void updateTextLog(String str) {
		textLog.append(str);
	}
	public synchronized void updateTextLog(byte[] b) {
	    char[] hexChars = new char[b.length * 2];
	    for (int j = 0; j < b.length; j++) {
	        int v = b[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	   textLog.append(new String(hexChars));
	}
	public synchronized void updateTextLogln(String str) {
		textLog.append(str);
		textLog.append("\n");
	}
	public synchronized void updateTextLogln(byte[] b) {
	    char[] hexChars = new char[b.length * 2];
	    for (int j = 0; j < b.length; j++) {
	        int v = b[j] & 0xFF;
	        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
	        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    }
	   textLog.append(new String(hexChars));
	   textLog.append("\n");
	}
}