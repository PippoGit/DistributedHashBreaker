/*

    TODO: 

    ! I metodi sulla classe AttackStatus dovrebbero essere ThreadSafe
    ! Cambiare il singleton con qualcosa che sia più adatto!

    - Revoke dalla dashboard di un bucket
    - Dopo Revoke bisogna abbassare il numero di collisioni
      Potrebbe essere necessario aggiungere il numero di collisioni trovate per ogni
      bucket dentro la struttura dati BucketRes
    - Adesso gli aggiornamenti alla dashboard vengono pushati tutti insieme 
      (alternativa: il metodo broadcast() viene sostituito da uno che pusha solo
       la roba che è stata effettivamente aggiornata, il client dovrebbe funzionare
       lo stesso)
    

 */
package it.unipi.ing.cds.dhbrmi;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import it.unipi.ing.cds.dhbrmi.client.iface.DHBRemoteClientInterface;
import it.unipi.ing.cds.dhbrmi.clientinfo.ClientInfo;
import it.unipi.ing.cds.dhbrmi.guardian.Guardian;
import it.unipi.ing.cds.dhbrmi.server.iface.DHBRemoteServerInterface;
import it.unipi.ing.cds.parameters.Parameters;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.DeploymentException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class DHBRemoteObj extends UnicastRemoteObject implements DHBRemoteServerInterface {
    
    private static final long serialVersionUID = 1L;
    
    // Resources for the RMI Server
    private String hashToBreak;
    
    private boolean attackInProgress;
    
    private String idAttack;			// id of current Attack
    
    // Queste variabili sono diventate più o meno inutili con la nuova gestione degli aggiornamenti...
    private double totalPercentage;		
    private int numCollisions;			
    private String etc;					
    //////////
    
    
    // Critica
    private Map<String, ClientInfo> clients;
    private List<Integer>  availableBuckets;
    private List<Integer>  inProgressBuckets;
    private List<Integer>  completedBuckets;
    //
    
    private Guardian guard;
    private Semaphore mutex;
    
    // Stuff to send data to Tomcat 
    private DHBWebSocketClient wsTomcat;
    
    public DHBRemoteObj() throws RemoteException {
        super();
        idAttack = Integer.toString(0);
        attackInProgress = false;
        initState();
        
        prompt("Connecting to Tomcat WebServer...");
        try {
            // Connect to Tomcat...
            wsTomcat = new DHBWebSocketClient(Parameters.NOTIFY_ENDPOINT);
            System.out.println("Connected!");
        } catch (DeploymentException | IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void prompt(String s) {
    	System.out.println("[SERVER] " + s);
    }
    
    public void initState() {
    	if(attackInProgress)
            cancelAttack();
    	
        // ( Available, inProgress, completed Buckets initialization
        availableBuckets = new ArrayList<Integer>();
        
        for(int i = 0; i < Parameters.NUM_OF_BUCKETS; i++) {
            availableBuckets.add(i);
        }
        
        Collections.shuffle(availableBuckets);
        availableBuckets = Collections.synchronizedList(availableBuckets);
        inProgressBuckets = Collections.synchronizedList(new ArrayList<Integer>());
        completedBuckets = Collections.synchronizedList(new ArrayList<Integer>());
        // )
        
        clients = new ConcurrentHashMap<String, ClientInfo>();
        	
        mutex = new Semaphore(1);
        guard = new Guardian(this, clients, mutex);
        
        this.idAttack = Integer.toString(Integer.parseInt(idAttack) + 1);
        this.totalPercentage = 0;
        this.numCollisions = 0;
        this.etc = "2h 27m";	// To properly update. Later
        attackInProgress = true;
        System.gc();
    }
    
    private void notifyBucketAction(int bucketId, String action) {
        Gson gson = new Gson();
        
        JsonObject msg = new JsonObject();
        JsonObject par = new JsonObject();

        msg.addProperty("action", action);
        par.addProperty("bucketId", bucketId);
        msg.add("params", par);
        try {            
           wsTomcat.sendText(gson.toJson(msg));
        } catch (IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void notifyAlloc(int bucketId, String worker) {      
        Gson gson = new Gson();
        
        JsonObject msg = new JsonObject();
        JsonObject par = new JsonObject();

        msg.addProperty("action", Parameters.NACT_BUCKET_ALLOC);
        par.addProperty("bucketId", bucketId);
        par.addProperty("worker", worker);
        msg.add("params", par);
        try {            
           wsTomcat.sendText(gson.toJson(msg));
        } catch (IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void notifyHeartbeat(int bucketId) {       
        notifyBucketAction(bucketId, Parameters.NACT_BUCKET_HEARTBEAT);
       
    }
    
    private void notifyCompleted(int bucketId) {
        notifyBucketAction(bucketId, Parameters.NACT_BUCKET_COMPLETED);
    }    

    private void notifyRevoke(int bucketId) {
        notifyBucketAction(bucketId, Parameters.NACT_BUCKET_REVOKE);
    }  
    
    private void notifyStats(int bucketId, long inspected, int collisions) {
        Gson gson = new Gson();
        
        JsonObject msg = new JsonObject();
        JsonObject par = new JsonObject();

        msg.addProperty("action", Parameters.NACT_BUCKET_STATS);
        par.addProperty("bucketId", bucketId);
        par.addProperty("percentage", (100*inspected)/Parameters.BUCKET_SIZE);
        par.addProperty("foundCollisions", collisions);
        msg.add("params", par);
        try {            
           wsTomcat.sendText(gson.toJson(msg));
        } catch (IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void notifyPlanAttack() {
        Gson gson = new Gson();
        JsonObject msg = new JsonObject();
        JsonObject par = new JsonObject();

        msg.addProperty("action", Parameters.NACT_PLAN_ATTACK);
        par.addProperty("idAttack", idAttack);
        msg.add("params", par);
        
        try {            
           wsTomcat.sendText(gson.toJson(msg));
        } catch (IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    


    public void sendStatistics(ArrayList<byte[]> partialCollisions, long inspected, String userID) throws RemoteException {
    	ClientInfo ci = clients.get(userID);
    	if(ci == null) {
            prompt("No client with username " + userID);
            return;
    	}
        ci.updateCollisions(partialCollisions);
        ci.updateInspected(inspected);
        ci.beats();
        
        // Notify Tomcat
        notifyHeartbeat(ci.getBucketNr());
               
        prompt(ci.getNickName() + " statistics: INSPECTED=" + inspected +" (TOTAL="+ ci.getInspected()+") " + " COLLISIONS=" + partialCollisions.size());
        
        // Notify Tomcat
        notifyStats(ci.getBucketNr(), ci.getInspected(), partialCollisions.size());
        
        if(ci.getInspected() == Parameters.BUCKET_SIZE) {	// all bucket has been inspected, then add it to completed buckets list
            prompt(ci.getNickName() + " has completed his bucket (" + ci.getBucketNr() + ")");
            completedBuckets.add(inProgressBuckets.remove(inProgressBuckets.indexOf(ci.getBucketNr())));
            
            // Notify Tomcat
            notifyCompleted(ci.getBucketNr());
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
            
            // Notify Tomcat
            notifyRevoke(ci.getBucketNr());
        } catch (MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }
    
    private void cancelAttack() {
    	prompt("Cancelling Attack");
    	try {
            mutex.acquire();
            for(ClientInfo ci : clients.values()) {
                if(!ci.isActive()) {
                    prompt("Proceeding to remove client " + ci.getId());
                    revoke(ci.getId());
                }
            }
        } catch (InterruptedException | RemoteException e) {
            e.printStackTrace();
        } finally {
            mutex.release();
            clients.clear();
        }
    	attackInProgress = false;
    }

    public String planAttack(String hash) throws RemoteException {
        this.hashToBreak = hash;
        initState();
        notifyPlanAttack();
        return this.idAttack;
    }
    
    public int getBucket(String userId) throws RemoteException {
    	int bucket = availableBuckets.remove(0);
    	prompt("Bucket: " + bucket);
    	inProgressBuckets.add(bucket);
    	
    	ClientInfo ci = clients.get(userId);
        prompt("Assigning new bucket to " + ci.getNickName());
        ci.setBucketNr(bucket);
        
        // Notify Tomcat
        notifyAlloc(bucket, ci.getNickName());
        
        return bucket;
    }
    
    public String getHash() throws RemoteException {
        return this.hashToBreak;
    }

    public String getId(String nickname, String hostIP, int hostPort) throws RemoteException {
        // Add the new client to the list of already joined clients
        if(attackInProgress) {
            prompt(nickname + "joined. IP=" + hostIP + "PORT=" + hostPort);
            ClientInfo ci = new ClientInfo(nickname, hostIP, hostPort);
            clients.put(ci.getId(), ci);

            if(!guard.isAlive()) {
                guard.start();
            }
            return ci.getId();
        }
        return null;
    }
    
    public boolean leave(String uuid) throws RemoteException {
        try {
            revoke(uuid);
            return true;
        } catch(Exception e) {
            prompt("Something went wrong in leave function");
            return false;
        }
    }
}