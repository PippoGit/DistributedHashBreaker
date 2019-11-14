package it.unipi.ing.cds.thread;
import it.unipi.ing.cds.gui.ClientGUI;
import it.unipi.ing.cds.util.Hash;
import it.unipi.ing.cds.util.Statistics;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class AnalyzerThread extends Thread {

	private int subBucket; // Also used as threadId
	private long lb;
	private long ub;
	private long iterator;
	private byte[] target;
	private Statistics stats;
	private ClientGUI clientGUI;
	private boolean running;
	
	private ArrayList<byte[]> collisions;

    public AnalyzerThread(long plaintextsPerThread, int subBucket, long start, byte[] target, Statistics stats) {
    	this.subBucket = subBucket;
        this.target = target;
        running = true;
        lb = start + subBucket*plaintextsPerThread;
        ub = start + (subBucket+1)*plaintextsPerThread;
        iterator = lb;
        collisions = new ArrayList<byte[]>();
        this.stats = stats;
        clientGUI = ClientGUI.getInstance();
    }

    public void run() {
        try {
        	clientGUI.updateTextLogln("Thread " + subBucket + ". Plaintexts: " + lb + "-" + ub);
        	int i;
	        while(iterator < ub && running) {
	        
	        	byte[] bytes = ByteBuffer.allocate(Long.BYTES).putLong(iterator).array();
	        	byte[] hash = Hash.getHash(bytes);
	        	
	        	//Compare
	        	for(i = 29; i < target.length; i++) {	// I want to compare only the last 3 bytes
	        		if(Byte.compare(target[i], hash[i]) != 0)
	        			break;
	        	}
	    		if(i == target.length) {
    	        	collisions.add(bytes);
	    		}
	    		iterator++;
	        }
	        clientGUI.updateTextLogln("Thread " + subBucket + " has finished its work");
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public int getID() { // NOTICE "getId" overrides java.lang.Thread metohod. "getID" is mine
    	return subBucket;
    }
    public long getInspected() {
    	return iterator-lb;
    }
    public void update() {
    	stats.update(subBucket, collisions, iterator-lb);
    	collisions.clear();
    }
    public void stopRunning() {
    	running = false;
    }
}