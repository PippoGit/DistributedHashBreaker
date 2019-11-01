package it.unipi.ing.cds.thread;
import it.unipi.ing.cds.parameters.Parameters;
import it.unipi.ing.cds.util.Statistics;

import java.util.List;
import java.util.concurrent.Semaphore;

public class StatisticsThread extends Thread {

	private boolean working;
	private Semaphore mutex;
	List<AnalyzerThread> threads;
	private Statistics stats;
	
	public StatisticsThread(Semaphore mutex, Statistics stats, List<AnalyzerThread> threads){
		working = true;
		this.mutex = mutex;
		this.stats = stats;
		this.threads = threads;
	}

	public void stopWorking() {
		this.working = false;
	}
	
	public void run() {
		while(working) {
			try {
				Thread.sleep(Parameters.SLEEP_TIME);
				mutex.acquire();
				for(AnalyzerThread t : threads)
					t.update();
				stats.showStatistics();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				 mutex.release();
			}
		}
	}
}