package it.unipi.ing.cds.util;

import java.util.ArrayList;
import java.util.HashMap;

import it.unipi.ing.cds.gui.ClientGUI;

public class Statistics {
	
	private class PerThreadStatistics{
		
		// PER THREAD STATISTICS
		private ArrayList<byte[]> collisions;
		private long inspected;
		
		public PerThreadStatistics(int id) {
			collisions = new ArrayList<byte[]>();
			inspected = 0;
		}
	}
	
	private int num;
	private HashMap<Integer,PerThreadStatistics> stats;
	private ClientGUI clientGUI;
	
	// GLOBAL STATISTICS
	private ArrayList<byte[]> collisions;
	private long executionTime;
	private long inspected;
	private long start;
	
	public Statistics(int num, ClientGUI clientGUI) {
		this.num = num;
		stats = new HashMap<Integer,PerThreadStatistics>(num);
		for(int i = 0; i < num; i++)
			stats.put(i, new PerThreadStatistics(i));
		collisions = new ArrayList<byte[]>();
		executionTime = 0;
		inspected = 0;
		start = System.currentTimeMillis();
		this.clientGUI = clientGUI;
	}
	public void update(int id, ArrayList<byte[]> partialCollisions, long inspected) {
		
		PerThreadStatistics tmp = stats.get(id);
		for(byte[] pc : partialCollisions)
			tmp.collisions.add(pc);
		tmp.inspected = inspected;
	}
	public void globalUpdate() {
		
		for(int i = 0; i < num; i++) {
			PerThreadStatistics t = stats.get(i);
			collisions.addAll(t.collisions);
			inspected += t.inspected;
			this.executionTime = System.currentTimeMillis() - start;
		}
	}
	
	public ArrayList<byte[]> getCollisions() {
		return collisions;
	}
	
	public long getExecutionTime() {
		return executionTime;
	}

	public long getInspected() {
		return inspected;
	}
	public void showStatistics() {
		for(int i = 0 ; i < num; i++)
			clientGUI.updateTextLog("Thread " + i + "\t\t\t");
		clientGUI.updateTextLog("\n");
		
		for(int i = 0 ; i < num; i++)
			clientGUI.updateTextLog("Number of collisions: " + stats.get(i).collisions.size() + "\t\t");
		clientGUI.updateTextLog("\n");
		
		for(int i = 0 ; i < num; i++)
			clientGUI.updateTextLog("Inspected plaintexts: " + stats.get(i).inspected + "\t");
		clientGUI.updateTextLog("\n");
	}
}