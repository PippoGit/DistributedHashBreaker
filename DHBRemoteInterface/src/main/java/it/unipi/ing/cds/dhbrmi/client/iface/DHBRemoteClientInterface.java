package it.unipi.ing.cds.dhbrmi.client.iface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DHBRemoteClientInterface extends Remote{
	
	public void revoke() throws RemoteException;

}
