package it.unipi.ing.cds.thread;

import it.unipi.ing.cds.util.Statistics;

public class TThread extends Thread{

	private boolean working;
	private Statistics stats;
	private int execTime;
	
	public TThread(Statistics stats){
		working = true;
		this.stats = stats;
		execTime = 0;
	}

	public void stopWorking() {
		this.working = false;
	}
	
	public void run() {
		while(working) {
			try {
				stats.updateClock(execTime++);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
