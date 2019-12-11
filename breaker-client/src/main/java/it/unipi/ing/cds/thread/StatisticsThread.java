package it.unipi.ing.cds.thread;
import it.unipi.ing.cds.parameters.Parameters;
import it.unipi.ing.cds.util.Request;
import it.unipi.ing.cds.util.Statistics;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.Semaphore;

public class StatisticsThread extends Thread {

	private boolean working;
	private Semaphore mutex;
	List<AnalyzerThread> threads;
	private Statistics stats;
	private Request req;
	
	public StatisticsThread(Semaphore mutex, Statistics stats, List<AnalyzerThread> threads){
		working = true;
		this.mutex = mutex;
		this.stats = stats;
		this.threads = threads;
		req = Request.getInstance();
	}

	public void stopWorking() {
		System.out.println("stopWorking() method call");
		this.working = false;
	}
	
	public void run() {
		int i = 0;
				
		while(working) {
			try {
				
				Thread.sleep(Parameters.SLEEP_TIME);
				for(AnalyzerThread t : threads)
					t.update();
				stats.updateGlobal();
				
				if(++i == Parameters.CYCLES) {
					long partialInspected = stats.getPartialInspected();
					System.out.println("Statistics: INSPECTED=" + stats.getInspected() + " PARTIAL=" + partialInspected);
					req.sendStatistics(stats.getPartialCollisions(), partialInspected);
					stats.clearPartialCollisions();
					i = 0;
				}
				//stats.showStatistics();
			} catch (InterruptedException | MalformedURLException e) {
				e.printStackTrace();
			}
		}
		// ONE LAST TIME BEFORE THE THREAD TERMINATES
		try {
			mutex.acquire();
			for(AnalyzerThread t : threads) {
				t.update();
			}
			stats.updateGlobal();
			long partialInspected = stats.getPartialInspected();
			System.out.println("Statistics: INSPECTED=" + stats.getInspected() + " PARTIAL=" + partialInspected);
			req.sendStatistics(stats.getPartialCollisions(), partialInspected);
			stats.clearPartialCollisions();
			//stats.showStatistics();
		} catch (InterruptedException | MalformedURLException e) {
			e.printStackTrace();
		} finally {
			 mutex.release();
		}
		System.out.println("Statistics threads terminated");
	}
}