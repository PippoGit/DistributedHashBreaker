package it.unipi.ing.cds.dhbrmi.guardian;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.Semaphore;

import it.unipi.ing.cds.dhbrmi.DHBRemoteObj;
import it.unipi.ing.cds.dhbrmi.clientinfo.ClientInfo;
import it.unipi.ing.cds.parameters.Parameters;

public class Guardian extends Thread {
	
	private static int gid = 0;
	
	private DHBRemoteObj server;
	private Map<String, ClientInfo> clients;
	private Semaphore mutex;
	
    public Guardian(DHBRemoteObj server, Map<String, ClientInfo> clients, Semaphore mutex) {
    	this.server = server;
    	this.clients = clients;
    	this.mutex = mutex;
    }

    public void run() {
    	prompt("Guardian thread activated");
        while(!clients.isEmpty()) {
        	try {
        		prompt("Analyzing line 29");
				Thread.sleep(Parameters.GUARD_TIME);
				prompt("Analyzing line 31");
	        	mutex.acquire();
	        	prompt("Analyzing line 33");
	        	for(ClientInfo ci : clients.values()) {
	        		prompt("Analyzing " + ci.getNickName());
	        		if(!ci.isActive()) {
	        			prompt("Client "+ ci.getNickName() + " is no longer active. Proceeding to remove it");
	        			server.revoke(ci.getId());
	        		}
	        	}
			} catch (InterruptedException | RemoteException e) {
				e.printStackTrace();
			}finally {
				mutex.release();
			}
        }
        prompt("No more clients, guardian process terminated");
    }
    private void prompt(String s) {
    	System.out.println("[GUARDIAN-" + gid++ + "] " + s);
    }
}