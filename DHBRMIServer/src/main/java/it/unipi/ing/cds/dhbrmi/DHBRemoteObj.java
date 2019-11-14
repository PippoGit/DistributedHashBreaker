/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbrmi;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import it.unipi.ing.cds.dhbrmi.client.iface.DHBRemoteClientInterface;
import it.unipi.ing.cds.dhbrmi.clientinfo.ClientInfo;
import it.unipi.ing.cds.dhbrmi.server.iface.DHBRemoteServerInterface;
import it.unipi.ing.cds.parameters.Parameters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.DeploymentException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DHBRemoteObj extends UnicastRemoteObject implements DHBRemoteServerInterface {
    
	private static final long serialVersionUID = 1L;
	// Resources for the RMI Server
    private static final int NUM_BUCKETS = 30;
    private String hashToBreak;
    
    private String idAttack;			// id of current Attack
    private double totalPercentage;		// percentage of completed bucket
    private int numCollisions;			// collision found
    private String etc;					// ???
    
    private int numAvailableBuckets;
    private int numWorkingBuckets;
    private int numCompletedBuckets;
    
    // Critica
    private Map<String, ClientInfo> clients;
    private List<Integer>  availableBuckets;
    private List<Integer>  inProgressBuckets;
    private List<Integer>  completedBuckets;
    //
    
    // Stuff to send data to Tomcat 
    private final static String NOTIFY_ENDPOINT = "ws://localhost:8080/DHBServer/notify";
    private DHBWebSocketClient wsTomcat;
    
    public DHBRemoteObj() throws RemoteException {
        super();
        idAttack = Integer.toString(0);
        clients = new HashMap<String, ClientInfo>();
        	
        /*
        try {
            // Connect to Tomcat...
            wsTomcat = new DHBWebSocketClient(NOTIFY_ENDPOINT);
            System.out.println("Connected!");
        } catch (DeploymentException | IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        System.out.println("Connecting to Tomcat WebServer...");
    }
    
    private void prompt(String s) {
    	System.out.println("[SERVER] " + s);
    }
    
    public void initState() {
        // ( Available Bucket initialization
        availableBuckets = new ArrayList<Integer>();
        for(int i = 0; i < Parameters.NUM_OF_BUCKETS; i++)
        	availableBuckets.add(i);
        Collections.shuffle(availableBuckets);
        // )
        inProgressBuckets = new ArrayList<Integer>();
        completedBuckets = new ArrayList<Integer>();
        
        this.idAttack = Integer.toString(Integer.parseInt(idAttack) + 1);
        this.totalPercentage = 0;
        this.numCollisions = 0;
        this.etc = "2h 27m";	// To properly update. Later
        this.numAvailableBuckets = Parameters.NUM_OF_BUCKETS;
        this.numWorkingBuckets = 0;
        this.numCompletedBuckets = 0;
    }
    
    private String getCurrentStateJSON() {
        JsonObject state = new JsonObject();
        Gson gson = new Gson();
        state.addProperty("idAttack", idAttack);
        state.addProperty("totalPercentage", totalPercentage);
        state.addProperty("numCollisions", numCollisions);
        state.addProperty("etc", etc);
        state.addProperty("numAvailableBuckets", numAvailableBuckets);
        state.addProperty("numWorkingBuckets", numWorkingBuckets);
        state.addProperty("numCompletedBuckets", numCompletedBuckets);
            
        JsonArray jbuckets = new JsonArray();
        for(int i=0; i<NUM_BUCKETS; i++) {
            JsonObject bucket = new JsonObject();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        
            // JUST A TEST HERE!
            bucket.addProperty("percentage", new Random().nextInt(101));
            bucket.addProperty("idWorker", "" + new Random().nextInt(101));
            bucket.addProperty("available", (Math.random() < 0.05));
            bucket.addProperty("dateAllocation", dateFormat.format(new Date()));
            bucket.addProperty("lastHeartbeat", dateFormat.format(new Date()));
            bucket.addProperty("dateCompleted", dateFormat.format(new Date()));
            jbuckets.add(bucket);
        }
        state.add("buckets", jbuckets);
        return gson.toJson(state);
    }
    
    private void notifyChanges(String msg) {
    	/*
        try {
           wsTomcat.sendText(msg);
        	
        } catch (IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    	prompt("Log");
    }

    public void putStatistics(String test) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void sendStatistics(ArrayList<byte[]> partialCollisions, long inspected, String userID) throws RemoteException {
    	ClientInfo ci = clients.get(userID);
        ci.updateCollisions(partialCollisions);
        ci.updateInspected(inspected);
        prompt(ci.getNickName() + " statistics: INSPECTED=" + inspected + " COLLISIONS=" + partialCollisions.size());
        if(inspected == Parameters.BUCKET_SIZE) {	// all bucket has been inspected, then add it to completed buckets list
        	completedBuckets.add(inProgressBuckets.remove(inProgressBuckets.indexOf(ci.getBucketNr())));
        	clients.remove(userID);
        }
    }
    
    public void revoke(String uuid) throws RemoteException {
        prompt("TEST REVOKING");
        ClientInfo ci = clients.get(uuid);
        try {
			DHBRemoteClientInterface client = (DHBRemoteClientInterface) Naming.lookup(ci.getUrl());
			client.revoke();
			//Remove bucket from in progress list and re-put it in among availables
			availableBuckets.add(inProgressBuckets.remove(inProgressBuckets.indexOf(ci.getBucketNr())));
			clients.remove(uuid);
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
    }

    public String planAttack(String hash) throws RemoteException {
        this.hashToBreak = hash;
        initState();
        notifyChanges(getCurrentStateJSON());
        return this.idAttack;
    }
    
    public int getBucket(String userId) throws RemoteException {
    	int bucket = availableBuckets.remove(0);
    	prompt("Bucket: " + bucket);
    	inProgressBuckets.add(bucket);
    	
    	ClientInfo ci = clients.get(userId);
        prompt("Assigning new bucket to " + ci.getNickName());
        ci.setBucketNr(bucket);
        
        // JUST A TEST HERE!
        this.etc = "update by " + userId + " (just to update something for now)";
        notifyChanges(getCurrentStateJSON());
        return bucket;
    }
    
    public String getHash() throws RemoteException {
        //return this.hashToBreak;
        return "dummy";
    }

	public String getId(String nickname, String hostIP, int hostPort) throws RemoteException {
		// Add the new client to the list of already joined clients
		prompt(nickname + "joined. IP=" + hostIP + "PORT=" + hostPort);
		ClientInfo ci = new ClientInfo(nickname, hostIP, hostPort);
		clients.put(ci.getId(), ci);
		return ci.getId();
	}
}