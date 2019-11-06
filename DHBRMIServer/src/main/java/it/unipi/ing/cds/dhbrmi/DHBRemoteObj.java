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
        System.out.println("New request from " + userId);
        return 42;
    }

    @Override
    public void putStatistics(String test) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getStatistics(String idAttack) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }    
}
