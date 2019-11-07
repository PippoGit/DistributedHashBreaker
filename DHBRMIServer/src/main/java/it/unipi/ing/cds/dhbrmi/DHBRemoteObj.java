/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbrmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import it.unipi.ing.cds.dhbrmi.iface.DHBRemoteInterface;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.DeploymentException;

public class DHBRemoteObj extends UnicastRemoteObject implements DHBRemoteInterface {
    
    // Resources for the RMI Server
    double [] buckets;
    
    // Stuff to send data to Tomcat 
    private final static String NOTIFY_ENDPOINT = "ws://localhost:8080/DHBServer/notify";
    private DHBWebSocketClient wsTomcat;
    
    public DHBRemoteObj() throws RemoteException {
        super();
        buckets = new double[10];
        
        try {
            // Connect to Tomcat...
            System.out.println("Connecting to Tomcat WebServer...");
            wsTomcat = new DHBWebSocketClient(NOTIFY_ENDPOINT);
        } catch (DeploymentException | IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void notifyChanges(String msg) {
        try {
            wsTomcat.sendText(msg);
        } catch (IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public double getBucket(String userId) throws RemoteException {
        System.out.println("New request from " + userId);
        notifyChanges(userId); // just a test to try real-time updates
        return 42;
    }

    @Override
    public void putStatistics(String test) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getStatistics(String idAttack) throws RemoteException {
        return "Test";
    }    
}
