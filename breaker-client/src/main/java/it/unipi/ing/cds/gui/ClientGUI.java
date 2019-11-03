package it.unipi.ing.cds.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.knowm.xchart.PieChart;
import org.knowm.xchart.XChartPanel;

import it.unipi.ing.cds.parameters.Parameters;
import it.unipi.ing.cds.worker.Worker;

import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.util.concurrent.Semaphore;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.GridLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

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
	private Semaphore mutex;
	private JLabel execTime;

	private static ClientGUI instance = null;
	private JTabbedPane tabbedPane;
	private JTextField textField;
	
	public static ClientGUI getInstance() {
		if(instance == null)
			instance = new ClientGUI();
		return instance;
	}
	
	private ClientGUI() {
		
		mutex = new Semaphore(1);
		
		setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 486, 272);
		contentPane = new JPanel();
		contentPane.setBackground(Color.GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		startBtn = new JButton("START");
		startBtn.setHorizontalTextPosition(SwingConstants.CENTER);
		startBtn.setFont(new Font("Consolas", Font.PLAIN, 12));
		startBtn.setBackground(SystemColor.textHighlight);
		startBtn.setForeground(SystemColor.inactiveCaptionBorder);
		startBtn.setFocusPainted(false);
		
		statPanel = new JPanel();
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		textField = new JTextField();
		textField.setColumns(10);
		
		execTime = new JLabel("Execution Time");
		execTime.setFont(new Font("Tahoma", Font.PLAIN, 22));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addGap(24)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, 146, GroupLayout.PREFERRED_SIZE)
					.addGap(33)
					.addComponent(startBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGap(26)
					.addComponent(execTime)
					.addGap(68))
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(statPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
						.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE))
					.addGap(25))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(textField, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
								.addComponent(startBtn, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
							.addGap(18))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(execTime)
							.addGap(18)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(statPanel, GroupLayout.DEFAULT_SIZE, 14, Short.MAX_VALUE))
		);
		
		scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tabbedPane.addTab("Log", scrollPane);
		
		textLog = new JTextArea();
		scrollPane.setViewportView(textLog);
		textLog.setEditable(false);
		textLog.setVisible(false);
		textLog.setBounds(10, 72, 417, 393);
		textLog.setLineWrap(true);
		textLog.setWrapStyleWord(true);
		
		globalStatPanel = new JPanel();
		tabbedPane.addTab("Global Stats", globalStatPanel);
		globalStatPanel.setVisible(false);
		scrollPane.setVisible(false);
		
		tabbedPane.setVisible(false);
		statPanel.setVisible(false);
		contentPane.setLayout(gl_contentPane);
		
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				setSize(453, 432);
				tabbedPane.setSelectedIndex(0);
				startBtn.setEnabled(false);
				textLog.setVisible(true);
				scrollPane.setVisible(true);
				tabbedPane.setVisible(true);
				
				contentPane.revalidate();
				
				// START THE JOB
				new Worker().start();
				startBtn.setEnabled(true);
				
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
		globalStatPanel.setVisible(false);
		globalStatPanel.setVisible(true);
		lastListSize = inspected;
	}
	
	public void updateClock(int secs) {
		int hours = secs / 3600;
		int minutes = (secs % 3600) / 60;
		int seconds = secs % 60;
		execTime.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
	}
	
	public void updateTextLog(String str) {
		try {
			mutex.acquire();
			textLog.append(str);
		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			mutex.release();
		}
	}
	public void updateTextLog(byte[] b) {
		try {
			mutex.acquire();
		    char[] hexChars = new char[b.length * 2];
		    for (int j = 0; j < b.length; j++) {
		        int v = b[j] & 0xFF;
		        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
		        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		    }
		    textLog.append(new String(hexChars));
		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			mutex.release();
		}
	}
	public void updateTextLogln(String str) {
		try {
			mutex.acquire();
			textLog.append(str);
			textLog.append("\n");
		} catch(InterruptedException e) {
			e.printStackTrace();
		} finally {
			mutex.release();
		}
	}
	public void updateTextLogln(byte[] b) {
		try {
			mutex.acquire();
		    char[] hexChars = new char[b.length * 2];
		    for (int j = 0; j < b.length; j++) {
		        int v = b[j] & 0xFF;
		        hexChars[j * 2] = HEX_ARRAY[v >>> 4];
		        hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		    }
		   textLog.append(new String(hexChars));
		   textLog.append("\n");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			mutex.release();
		}
	}
}