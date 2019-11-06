/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbrmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import it.unipi.ing.cds.dhbrmi.iface.DHBRemoteInterface;


public class DHBRemoteObj extends UnicastRemoteObject implements DHBRemoteInterface {
    
    // Resources for the RMI Server
    double [] buckets;
    
    public DHBRemoteObj() throws RemoteException {
        super();
        buckets = new double[10];
    }

    @Override
    public double getBucket(String userId) throws RemoteException {
        // The idea is something like this:
        // First the User sends an update to the server. The RMI Server 
        // changes its local data structures and push a notification to the
        // WebServer which should push updates to the Admin through WebSockets
        System.out.println("New request from " + userId);
        return 42;
    }

    @Override
    public void putStatistics(String test) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStatistics(String idAttack) throws RemoteException {
        return "Test";
    }    
}
