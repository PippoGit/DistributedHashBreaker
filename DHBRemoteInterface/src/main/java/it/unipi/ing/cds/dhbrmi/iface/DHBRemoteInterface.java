/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbrmi.iface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DHBRemoteInterface extends Remote {
    public double getBucket(String userId)       throws RemoteException;
    public void   putStatistics(String test)     throws RemoteException;
    public void   getStatistics(String idAttack) throws RemoteException;
}
