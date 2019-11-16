/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbrmi.server.iface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface DHBRemoteServerInterface extends Remote {
    public int getBucket(String userId) throws RemoteException;
    public void sendStatistics(ArrayList<byte[]> partialCollisions, long inspected, String ID) throws RemoteException;
    public String planAttack(String hash) throws RemoteException;
    public String getHash() throws RemoteException;
    public String getId(String nickname, String hostIP, int hostPort) throws RemoteException;
    public boolean leave(String uuid) throws RemoteException;
}