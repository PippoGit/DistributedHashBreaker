package it.unipi.ing.cds.dhbrmi.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import it.unipi.ing.cds.dhbrmi.client.iface.DHBRemoteClientInterface;
import it.unipi.ing.cds.worker.Worker;

public class DHBRemoteObj extends UnicastRemoteObject implements DHBRemoteClientInterface {
	
	private static final long serialVersionUID = 1L;
	private Worker worker;
	
	public DHBRemoteObj(Worker worker) throws RemoteException {
		super();
		this.worker = worker;
	}
	
	private void prompt(String s) {
		System.out.println("[CLIENT] " + s);
	}
	
	public void revoke() throws RemoteException {
		prompt("Server revoking bucket to thread " + worker.getId());
		worker.terminateAll();
		worker.resetBtn();
	}
}