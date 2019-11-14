package it.unipi.ing.cds.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

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
	private ArrayList<byte[]> partialCollisions;	// Used to send updates to the server
	private long executionTime;
	private long inspected;
	private long partialInspected;
	private long start;
	
	public Statistics(int num) {
		this.num = num;
		stats = new HashMap<Integer,PerThreadStatistics>(num);
		for(int i = 0; i < num; i++)
			stats.put(i, new PerThreadStatistics(i));
		collisions = new ArrayList<byte[]>();
		partialCollisions = new ArrayList<byte[]>();
		executionTime = 0;
		inspected = 0;
		partialInspected = 0;
		start = System.currentTimeMillis();
		clientGUI = ClientGUI.getInstance();
	}
	public void update(int id, ArrayList<byte[]> partialCollisions, long inspected) {
		// Per thread Statistics
		PerThreadStatistics tmp = stats.get(id);
		for(byte[] pc : partialCollisions)
			tmp.collisions.add(pc);
		tmp.inspected = inspected;
		
		// Global Statistics
		collisions.addAll(partialCollisions);
		
		this.partialCollisions.addAll(partialCollisions);
		
		this.partialInspected = 0;
		for(Entry<Integer, PerThreadStatistics> item:stats.entrySet())
			this.partialInspected += item.getValue().inspected;
		this.inspected += tmp.inspected;
		
		executionTime = System.currentTimeMillis() - start;
		
		clientGUI.updatePerThreadStatistics(id, tmp.collisions.size(), inspected);
	}
	
	public ArrayList<byte[]> getPartialCollisions(){
		return partialCollisions;
	}
	public void clearPartialCollisions() {
		partialCollisions.clear();
	}
	
	public void updateGlobal() {
		clientGUI.updateGlobalStatistics(collisions.size(), inspected);
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
	public long getPartialInspected() {
		return partialInspected;
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
	public void updateClock(int secs) {
		clientGUI.updateClock(secs);
	}
}