package it.unipi.ing.cds.dhbrmi.guardian;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.Semaphore;

import it.unipi.ing.cds.dhbrmi.DHBRemoteObj;
import it.unipi.ing.cds.dhbrmi.clientinfo.ClientInfo;
import it.unipi.ing.cds.parameters.Parameters;

public class Guardian extends Thread {
	
	private static int gid = -1;
	
	private DHBRemoteObj server;
	private Map<String, ClientInfo> clients;
	private Semaphore mutex;
	
    public Guardian(DHBRemoteObj server, Map<String, ClientInfo> clients, Semaphore mutex) {
    	gid++;
    	this.server = server;
    	this.clients = clients;
    	this.mutex = mutex;
    }

    public void run() {
    	prompt("Guardian thread activated");
        while(!clients.isEmpty()) {
        	try {
				Thread.sleep(Parameters.GUARD_TIME);
	        	mutex.acquire();
	        	for(ClientInfo ci : clients.values()) {
	        		prompt("Analyzing " + ci.getNickName());
	        		if(!ci.isActive()) {
	        			String id = ci.getId();
	        			prompt("Client "+ ci.getNickName() + " ( " + id + ") is no longer active. Proceeding to remove it");
	        			try {
							server.revoke(id);
						} catch (RemoteException e) {
							prompt("Client " + ci.getNickName() + " disconnected. Proceeding to remove it");
							server.revokeDisconnected(ci.getId());
							//e.printStackTrace();
						}
	        		}
	        	}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}finally {
				mutex.release();
			}
        }
        server.guardianTerminate();
        prompt("No more clients, guardian process terminated");
    }
    private void prompt(String s) {
    	System.out.println("[GUARDIAN-" + gid + "] " + s);
    }
}