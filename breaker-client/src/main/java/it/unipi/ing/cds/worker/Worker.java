package it.unipi.ing.cds.worker;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import it.unipi.ing.cds.gui.ClientGUI;
import it.unipi.ing.cds.parameters.Parameters;
import it.unipi.ing.cds.thread.AnalyzerThread;
import it.unipi.ing.cds.thread.StatisticsThread;
import it.unipi.ing.cds.util.Hash;
import it.unipi.ing.cds.util.Request;
import it.unipi.ing.cds.util.Statistics;

public class Worker extends Thread{

	private Request req;
	private Hash hasher;
	private byte[] target;
	private int cores;
	private int NUMBER_OF_THREADS;
	private Semaphore mutex;
	private Statistics stats;
	private ClientGUI clientGUI;
	
    public Worker(ClientGUI clientGUI) {
    	req = new Request();
    	hasher = new Hash();
    	target = req.getTarget();
        NUMBER_OF_THREADS = 1;
        cores = Runtime.getRuntime().availableProcessors();
        while(2*NUMBER_OF_THREADS <= cores)
        	NUMBER_OF_THREADS*=2;
        mutex = new Semaphore(1);
        this.clientGUI = clientGUI;
        stats = new Statistics(NUMBER_OF_THREADS, clientGUI);
       
        
    }
    
    public void run() {
    	try {
    		clientGUI.initPies(NUMBER_OF_THREADS);
    		clientGUI.initGlobal();
	    	clientGUI.updateTextLogln(target);
	    	clientGUI.updateTextLogln("Target length: " + target.length);
	        
	    	int bucketNr = req.getBucketNr();
	        long start;
	        List<AnalyzerThread> threads;
	        
	    	clientGUI.updateTextLogln("Start Analying bucket Nr. " + bucketNr);
	    	Worker.sleep(1000);
	    	
	    	start = bucketNr*((long)Math.pow(2, Parameters.BUCKET_BITS));
	    	threads = createAnalyzerThreads(start);
	        StatisticsThread statThread = new StatisticsThread(mutex, stats, threads);
	        statThread.setPriority(Thread.MAX_PRIORITY);
	        statThread.start();
	        
	        for (AnalyzerThread thread : threads) 
	            thread.start();
	        
	        for (AnalyzerThread thread : threads)
				thread.join();

	        statThread.stopWorking();
	        statThread.join();
	        
	        // SHOW GLOBAL STATISTICS
	        ArrayList<byte[]> collisions = stats.getCollisions();
	        clientGUI.updateTextLogln("Global Statistics: Collisions = " + collisions.size() + " Inspected = " + stats.getInspected() + " Execution Time = " + stats.getExecutionTime());
	        byte[] tmp = null;
	        for (byte[] c : collisions) {
	    		clientGUI.updateTextLogln(c);
				try {
					tmp = hasher.getHash(c);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
	    		clientGUI.updateTextLogln(tmp);
	    	}
    	} catch(InterruptedException e) {
    		e.printStackTrace();
    	}
    }
    private List<AnalyzerThread> createAnalyzerThreads(long start) {
        List<AnalyzerThread> analyzerThreads = new ArrayList<AnalyzerThread>();
        clientGUI.updateTextLogln("Number of cores: " + cores + "\t Number of threads: " + NUMBER_OF_THREADS);
        long plaintextsPerThread = Parameters.BUCKET_SIZE / NUMBER_OF_THREADS;
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            AnalyzerThread thread = new AnalyzerThread(plaintextsPerThread, i, start, target, mutex, stats, clientGUI);
            analyzerThreads.add(thread);
        }
        return analyzerThreads;
    }
}