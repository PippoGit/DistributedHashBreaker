package it.unipi.ing.cds.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;

import it.unipi.ing.cds.parameters.Parameters;
import it.unipi.ing.cds.worker.Worker;

import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.SequentialGroup;
import java.util.List;
import javax.swing.LayoutStyle.ComponentPlacement;

public class ClientGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	private JPanel contentPane;
	private JButton startBtn;
	private JTextArea textLog;
	private JScrollPane scrollPane;
	private JPanel statPanel;
	private JPanel globalStatPanel;
	private PieChart[] pies;
	private PieChart globalPie;
	private int nThreads;
	private long lastListSize = 0;

	public ClientGUI() {
		setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 100);
		contentPane = new JPanel();
		contentPane.setBackground(Color.GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		startBtn = new JButton("START");
		startBtn.setFont(new Font("Consolas", Font.PLAIN, 12));
		startBtn.setBackground(SystemColor.textHighlight);
		startBtn.setForeground(SystemColor.inactiveCaptionBorder);
		startBtn.setFocusPainted(false);
		
		textLog = new JTextArea();
		textLog.setEditable(false);
		textLog.setVisible(false);
		textLog.setBounds(10, 72, 417, 393);
		textLog.setLineWrap(true);
		textLog.setWrapStyleWord(true);
		
		statPanel = new JPanel();
		
		scrollPane = new JScrollPane(textLog, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		globalStatPanel = new JPanel();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(164)
					.addComponent(startBtn, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
					.addGap(174))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(statPanel, GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(5)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 208, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(globalStatPanel, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(startBtn, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(globalStatPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
					.addGap(18)
					.addComponent(statPanel, GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		statPanel.setVisible(false);
		globalStatPanel.setVisible(false);
		contentPane.setLayout(gl_contentPane);
		scrollPane.setVisible(false);
		
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				setSize(453, 432);
				startBtn.setEnabled(false);
				textLog.setVisible(true);
				scrollPane.setVisible(true);
				
				contentPane.revalidate();
				
				// START THE JOB
				new Worker(ClientGUI.this).start();
				
			}
		});
	}
	
	public void initGlobal() {
		globalStatPanel.removeAll();
		globalStatPanel.setLayout(new BorderLayout());
		globalPie = new PieChart(globalStatPanel.getWidth(), globalStatPanel.getHeight());

		globalPie.addSeries("Remaining", (double) Parameters.BUCKET_SIZE);
		globalPie.addSeries("Inspected", (double) 0);
		globalPie.setTitle("Stats - 0 collisions found");
		globalPie.getStyler().setLegendVisible(true);
		globalStatPanel.add(new XChartPanel<PieChart>(globalPie));
		globalStatPanel.setVisible(true);
	}
	
	public void initPies(int nThreads) {
		statPanel.removeAll();
		this.nThreads = nThreads;
		double tmp = Math.log((double) nThreads)/Math.log((double)2);
		statPanel.setLayout(new GridLayout((int)Math.pow(2, Math.floor(tmp/2.0)), (int)Math.pow(2, Math.ceil(tmp/2.0))));
		pies = new PieChart[nThreads];

		for(int i = 0; i < nThreads; i++) {
			PieChart pivot = new PieChart(400, 400);
			pivot.addSeries("Remaining", (double) Parameters.BUCKET_SIZE / nThreads);
			pivot.addSeries("Inspected", (double) 0);
			pivot.setTitle("Thread " + i + " - 0 collisions found");
			pivot.getStyler().setLegendVisible(false);
			pies[i] = pivot;
			statPanel.add(new XChartPanel<PieChart>(pivot));
		}
		statPanel.setVisible(true);
	}
	
	public void updatePerThreadStatistics(int id, int numberOfCollisions, long inspected) {
		PieChart pivot = pies[id];
		pivot.updatePieSeries("Remaining", (double)(Parameters.BUCKET_SIZE / nThreads - inspected));
		pivot.updatePieSeries("Inspected", (double)inspected);
		pivot.setTitle("Thread " + id + " - " + numberOfCollisions + " collisions found");
		statPanel.repaint();
	}

	public void updateGlobalStatistics(int numberOfCollisions, long inspected) {
		globalPie.updatePieSeries("Remaining", (double)(Parameters.BUCKET_SIZE - inspected + lastListSize));
		globalPie.updatePieSeries("Inspected", (double)(inspected - lastListSize));
		globalPie.setTitle("Global Stats " + "- " + numberOfCollisions + " collisions found");
		globalStatPanel.repaint();
		lastListSize = inspected;
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