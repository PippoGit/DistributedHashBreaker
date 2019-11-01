package it.unipi.ing.cds.thread;
import it.unipi.ing.cds.gui.ClientGUI;
import it.unipi.ing.cds.util.Hash;
import it.unipi.ing.cds.util.Statistics;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class AnalyzerThread extends Thread {

	private int subBucket; // Also used as threadId
	private long lb;
	private long ub;
	private long iterator;
	private byte[] target;
	private Semaphore mutex;
	private Hash hasher;
	private Statistics stats;
	private ClientGUI clientGUI;
	
	private ArrayList<byte[]> collisions;

    public AnalyzerThread(long plaintextsPerThread, int subBucket, long start, byte[] target, Semaphore mutex, Statistics stats, ClientGUI clientGUI) {
        this.subBucket = subBucket;
        this.target = target;
        lb = start + subBucket*plaintextsPerThread;
        ub = start + (subBucket+1)*plaintextsPerThread;
        iterator = lb;
        this.mutex = mutex;
        hasher = new Hash();
        collisions = new ArrayList<byte[]>();
        this.stats = stats;
        this.clientGUI = clientGUI;
    }

    public void run() {
        try {
        	clientGUI.updateTextLogln("Thread " + subBucket + ". Plaintexts: " + lb + "-" + ub);
        	int i;
	        while(iterator < ub) {
	        
	        	byte[] bytes = ByteBuffer.allocate(Long.BYTES).putLong(iterator).array();
	        	byte[] hash = hasher.getHash(bytes);
	        	
	        	//Compare
	        	for(i = 29; i < target.length; i++) {	// I want to compare only the last 3 bytes
	        		if(Byte.compare(target[i], hash[i]) != 0)
	        			break;
	        	}
	    		if(i == target.length) {
	    	        try {
	    	        	mutex.acquire();  // block until condition holds
	    	        	collisions.add(bytes);
	    	        } finally {
	    	        	mutex.release();
	    	        }
	    		}
	    		iterator++;
	        }
	        clientGUI.updateTextLogln("Thread " + subBucket + " has finished its work");
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void update() {
    	stats.update(subBucket, collisions, iterator-lb);
    	collisions.clear();
    }
}
