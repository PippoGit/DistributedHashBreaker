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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DHBRemoteObj extends UnicastRemoteObject implements DHBRemoteInterface {
    
    // Resources for the RMI Server
    private static final int NUM_BUCKETS = 30;
    private String hashToBreak;
    
    private String idAttack;
    private double totalPercentage;
    private int numCollisions;
    private String etc;
    
    private int numAvailableBuckets;
    private int numWorkingBuckets;
    private int numCompletedBuckets;
    
    double [] buckets;
    
    // Stuff to send data to Tomcat 
    private final static String NOTIFY_ENDPOINT = "ws://localhost:8080/DHBServer/notify";
    private DHBWebSocketClient wsTomcat;
    
    public DHBRemoteObj() throws RemoteException {
        super();
        // initState();
        
        try {
            // Connect to Tomcat...
            System.out.println("Connecting to Tomcat WebServer...");
            wsTomcat = new DHBWebSocketClient(NOTIFY_ENDPOINT);
            
            // Send current status only if attack is planned
            // wsTomcat.sendText(getCurrentStateJSON());
        } catch (DeploymentException | IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void initState() {
        buckets = new double[NUM_BUCKETS];
        this.idAttack            = "001";
        this.totalPercentage     = 45;
        this.numCollisions       = 1234;
        this.etc                 = "2h 27m";
        this.numAvailableBuckets = 10;
        this.numWorkingBuckets   = 20;
        this.numCompletedBuckets = 15;
    }
    
    
    private String getCurrentStateJSON() {
        JsonObject state = new JsonObject();
        Gson gson = new Gson();
        state.addProperty("idAttack",            idAttack);
        state.addProperty("totalPercentage",     totalPercentage);
        state.addProperty("numCollisions",       numCollisions);
        state.addProperty("etc",                 etc);
        state.addProperty("numAvailableBuckets", numAvailableBuckets);
        state.addProperty("numWorkingBuckets",   numWorkingBuckets);
        state.addProperty("numCompletedBuckets", numCompletedBuckets);
            
        JsonArray jbuckets = new JsonArray();
        for(int i=0; i<NUM_BUCKETS; i++) {
            JsonObject bucket = new JsonObject();
            bucket.addProperty("percentage", new Random().nextInt(101));
            bucket.addProperty("idWorker", "" + new Random().nextInt(101));
            bucket.addProperty("available", (Math.random() < 0.05));
            bucket.addProperty("dateAllocation", new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date()));
            bucket.addProperty("lastHeartbeat", new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date()));
            bucket.addProperty("dateCompleted", new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date()));
            jbuckets.add(bucket);
        }
        state.add("buckets", jbuckets);
        return gson.toJson(state);
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
        
        // JUST A TEST HERE!
        this.etc = userId;
        this.totalPercentage = 99;
        notifyChanges(getCurrentStateJSON());
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

    @Override
    public String planAttack(String hash) throws RemoteException {
        buckets = new double[NUM_BUCKETS];
        this.idAttack            = "";
        this.totalPercentage     = 0;
        this.numCollisions       = 0;
        this.etc                 = "tbd";
        this.numAvailableBuckets = NUM_BUCKETS;
        this.numWorkingBuckets   = 0;
        this.numCompletedBuckets = 0;
        this.hashToBreak         = hash;
        
        // just a test...
        initState();
        notifyChanges(getCurrentStateJSON());
        return this.idAttack;
    }

    @Override
    public String getHash() throws RemoteException {
        return this.hashToBreak;
    }
}
