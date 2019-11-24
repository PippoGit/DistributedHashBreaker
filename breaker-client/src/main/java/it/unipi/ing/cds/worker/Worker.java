package it.unipi.ing.cds.worker;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import it.unipi.ing.cds.dhbrmi.client.DHBRemoteObj;
import it.unipi.ing.cds.dhbrmi.client.iface.DHBRemoteClientInterface;
import it.unipi.ing.cds.gui.ClientGUI;
import it.unipi.ing.cds.parameters.Parameters;
import it.unipi.ing.cds.thread.AnalyzerThread;
import it.unipi.ing.cds.thread.TThread;
import it.unipi.ing.cds.thread.StatisticsThread;
import it.unipi.ing.cds.util.AvailablePortFinder;
import it.unipi.ing.cds.util.Hash;
import it.unipi.ing.cds.util.Request;
import it.unipi.ing.cds.util.Statistics;

public class Worker extends Thread{

	private Request req;
	private byte[] target;
	private int cores;
	private int NUMBER_OF_THREADS;
	private Semaphore mutex;
	private Statistics stats;
	private ClientGUI clientGUI;
	private String nickname;
	private int hostPort;
	private List<AnalyzerThread> threads;
	private StatisticsThread statThread;
	private TThread tThread;
	
    public Worker(String nickname) {
        NUMBER_OF_THREADS = 1;
        cores = Runtime.getRuntime().availableProcessors();
        while(2*NUMBER_OF_THREADS <= cores)
        	NUMBER_OF_THREADS*=2;
        mutex = new Semaphore(1);
        clientGUI = ClientGUI.getInstance();
        stats = new Statistics(NUMBER_OF_THREADS);
        this.nickname = nickname;
        hostPort = AvailablePortFinder.getNextAvailable();
    }
    
    public void run() {
    	setPriority(Thread.MAX_PRIORITY);
    	long start;
    	String sessionID;
    	try {
    		req = Request.getInstance(nickname);
    		sessionID = req.getId(nickname, Parameters.MYREGISTRY_HOST, hostPort);
	    	if(sessionID.equals(Parameters.noAttackPlanned.toString())) {
	    		clientGUI.updateTextLogln("NO ATTACK IS PLANNED");
	    		resetBtn();
	    		return;
	    	}
	    	if(sessionID.equals(Parameters.noAvailableBucket.toString())) {
	    		clientGUI.updateTextLogln("NO AVAILABLE BUCKETS");
	    		resetBtn();
	    		return;
	    	}
    		
    		// ( CLIENT INTERFACE INITIALIZATION (
    		System.out.println("[CLIENT] Registering " + nickname + " (" + Parameters.MYREGISTRY_HOST + "/" + hostPort + ")");
            LocateRegistry.createRegistry(hostPort);
            System.out.println("Registry created - port "+Integer.toString(hostPort));
    		DHBRemoteClientInterface clientInterface = new DHBRemoteObj(this);
    		Naming.rebind("//"+Parameters.MYREGISTRY_HOST+":"+Integer.toString(hostPort)+"/"+nickname, clientInterface);
			// )
	    	
	    	
	    	int bucketNr = req.getBucketNr();
	    	
	    	target = req.getTarget();
	    	clientGUI.updateTextLogln(nickname + " joined");
    		clientGUI.initPies(NUMBER_OF_THREADS);
    		clientGUI.initGlobal();
	    	clientGUI.updateTextLogln(target);
	    	clientGUI.updateTextLogln("Target length: " + target.length);
	    	clientGUI.updateTextLogln("Start Analying bucket Nr. " + bucketNr);
	    	
	    	start = bucketNr*((long)Math.pow(2, Parameters.BUCKET_BITS));
	    	threads = createAnalyzerThreads(start);
	        statThread = new StatisticsThread(mutex, stats, threads);
	        statThread.setPriority(Thread.NORM_PRIORITY);
	        statThread.start();
	        
	        tThread = new TThread(stats);
	        tThread.setPriority(Thread.MIN_PRIORITY);
	        tThread.start();
	        
	        for (AnalyzerThread thread : threads) 
	            thread.start();
	        
	        for (AnalyzerThread thread : threads)
				thread.join();

	        statThread.stopWorking();
	        tThread.stopWorking();
	        tThread.join();
	        statThread.join();
	        
	        // SHOW GLOBAL STATISTICS
	        // -------- da testare --------- (reset btn)
	        resetBtn();
	        
	        ArrayList<byte[]> collisions = stats.getCollisions();
	        clientGUI.updateTextLogln("Global Statistics: Collisions = " + collisions.size() + " Inspected = " + stats.getInspected() + " Execution Time = " + stats.getExecutionTime());
	        byte[] tmp = null;
	        for (byte[] c : collisions) {
	    		clientGUI.updateTextLogln(c);
				try {
					tmp = Hash.getHash(c);
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
	    		clientGUI.updateTextLogln(tmp);
	    	}
    	} catch(InterruptedException | MalformedURLException | RemoteException e) {
    		e.printStackTrace();
    	}
    	//clientGUI.enableButton();
    	System.gc();
    }
    private List<AnalyzerThread> createAnalyzerThreads(long start) {
        List<AnalyzerThread> analyzerThreads = new ArrayList<AnalyzerThread>();
        clientGUI.updateTextLogln("Number of cores: " + cores + "\t Number of threads: " + NUMBER_OF_THREADS);
        long plaintextsPerThread = Parameters.BUCKET_SIZE / NUMBER_OF_THREADS;
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            AnalyzerThread thread = new AnalyzerThread(plaintextsPerThread, i, start, target, stats);
            thread.setPriority(Thread.MIN_PRIORITY);
            analyzerThreads.add(thread);
        }
        return analyzerThreads;
    }
    public void terminateAll() {
        for (AnalyzerThread thread : threads)
            thread.stopRunning();
        statThread.stopWorking();
        tThread.stopWorking();
        
		try {
			for (AnalyzerThread thread : threads)
				thread.join();
	        statThread.join();
	        tThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public void leave() {
    	terminateAll();
    	req.leave();
    }
    public void resetBtn() {
    	clientGUI.setStartAction();
    }
}