/*

    TODO: 

    ! Cambiato il singleton con un'implementazione threadsafe, 
      però forse non è ancora la cosa migliore?

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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Map;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class DHBRemoteObj extends UnicastRemoteObject implements DHBRemoteServerInterface {
    
    private static final boolean SHOULD_NOTIFY_TOMCAT = true;
    
    private static final long serialVersionUID = 1L;
    
    // Resources for the RMI Server
    private String hashToBreak;
    
    private boolean attackInProgress;
    
    private String idAttack;			// id of current Attack
    
    // Critica
    private Map<String, ClientInfo> clients;
    private List<Integer>  availableBuckets;
    private List<Integer>  inProgressBuckets;
    private List<Integer>  completedBuckets;
    //
    
    private boolean guardActive;
    private Semaphore mutex;
    
    // Stuff to send data to Tomcat 
    private DHBWebSocketClient wsTomcat;
    
    public DHBRemoteObj() throws RemoteException {
        super();
        idAttack = Integer.toString(0);
        attackInProgress = false;
        initState();
        
        if(SHOULD_NOTIFY_TOMCAT) {
            prompt("Connecting to Tomcat WebServer...");
            try {
                // Connect to Tomcat...
                wsTomcat = new DHBWebSocketClient(Parameters.NOTIFY_ENDPOINT);
                System.out.println("Connected!");
            } catch (DeploymentException | IOException ex) {
                Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
            }
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
        guardActive = false;
        
        this.idAttack = Integer.toString(Integer.parseInt(idAttack) + 1);
        attackInProgress = true;
        System.gc();
    }
    
    private void notifyTomcat(String action, JsonObject par) {
        Gson gson = new Gson();
        JsonArray request = new JsonArray();

        request.add(action);
        request.add(par);
                
        try {            
           wsTomcat.sendText(gson.toJson(request));
        } catch (IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void notifyAlloc(int idBucket, String worker, String UUIDWorker) {      
        JsonObject par = new JsonObject();
        par.addProperty("bucket", idBucket);
        par.addProperty("worker", worker);
        par.addProperty("UUIDWorker", UUIDWorker);
        notifyTomcat(Parameters.NACT_BUCKET_ALLOC, par);
    }
    
    private void notifyHeartbeat(int idBucket) {              
        JsonObject par = new JsonObject();
        par.addProperty("bucket", idBucket);
        notifyTomcat(Parameters.NACT_BUCKET_HEARTBEAT, par);
    }
    
    private void notifyCompleted(int idBucket) {        
        JsonObject par = new JsonObject();
        par.addProperty("bucket", idBucket);
        notifyTomcat(Parameters.NACT_BUCKET_COMPLETED, par);
    }    

    private void notifyRevoke(int idBucket) {        
        JsonObject par = new JsonObject();
        par.addProperty("bucket", idBucket);
        notifyTomcat(Parameters.NACT_BUCKET_REVOKE, par);
    }  
    
    private void notifyStats(int idBucket, long inspected, int collisions) {       
        JsonObject par = new JsonObject();
        par.addProperty("bucket", idBucket);
        par.addProperty("percentage", (100*inspected)/Parameters.BUCKET_SIZE);
        par.addProperty("foundCollisions", collisions);
        notifyTomcat(Parameters.NACT_BUCKET_STATS, par);
    }
    
    private void notifyPlanAttack() {       
        JsonObject par = new JsonObject();
        par.addProperty("attack", idAttack);
        notifyTomcat(Parameters.NACT_PLAN_ATTACK, par);
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
        if(SHOULD_NOTIFY_TOMCAT)
            notifyHeartbeat(ci.getBucketNr());
               
        prompt(ci.getNickName() + " statistics: INSPECTED=" + inspected +" (TOTAL="+ ci.getInspected()+") " + " COLLISIONS=" + partialCollisions.size());
        
        // Notify Tomcat
        if(SHOULD_NOTIFY_TOMCAT)
            notifyStats(ci.getBucketNr(), ci.getInspected(), partialCollisions.size());
        
        if(ci.getInspected() == Parameters.BUCKET_SIZE) {	// all bucket has been inspected, then add it to completed buckets list
            prompt(ci.getNickName() + " has completed his bucket (" + ci.getBucketNr() + ")");
            completedBuckets.add(inProgressBuckets.remove(inProgressBuckets.indexOf(ci.getBucketNr())));
            
            // Notify Tomcat
            if(SHOULD_NOTIFY_TOMCAT)
                notifyCompleted(ci.getBucketNr());
            clients.remove(userID);
        }
    }
    
    public void revoke(String uuid) throws RemoteException {
        ClientInfo ci = clients.get(uuid);
        prompt("Revoking bucket to client " + ci.getNickName());
        try {
            DHBRemoteClientInterface client = (DHBRemoteClientInterface) Naming.lookup(ci.getUrl());
            client.revoke();
            //Remove bucket from in progress list and re-put it in among availables
            availableBuckets.add(inProgressBuckets.remove(inProgressBuckets.indexOf(ci.getBucketNr())));
            clients.remove(uuid);
            
            // Notify Tomcat
            if(SHOULD_NOTIFY_TOMCAT)
                notifyRevoke(ci.getBucketNr());
        } catch (MalformedURLException | NotBoundException e) {
            e.printStackTrace();
        }
    }
    public void revokeDisconnected(String uuid) {
        ClientInfo ci = clients.get(uuid);
        prompt("Revoking bucket (for lost connection) to client " + ci.getNickName());
        //Remove bucket from in progress list and re-put it in among availables
		availableBuckets.add(inProgressBuckets.remove(inProgressBuckets.indexOf(ci.getBucketNr())));
		clients.remove(uuid);
                
        // Notify Tomcat
        if(SHOULD_NOTIFY_TOMCAT)
            notifyRevoke(ci.getBucketNr());
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
        if(SHOULD_NOTIFY_TOMCAT) {
            this.hashToBreak = hash;
            initState();
            notifyPlanAttack();
            return this.idAttack;
        } else
            return "dummy";
    }
    
    public int getBucket(String userId) throws RemoteException {
    	int bucket = availableBuckets.remove(0);
    	prompt("Bucket: " + bucket);
    	inProgressBuckets.add(bucket);
    	
    	ClientInfo ci = clients.get(userId);
        prompt("Assigning new bucket to " + ci.getNickName());
        ci.setBucketNr(bucket);
        
        // Notify Tomcat
        if(SHOULD_NOTIFY_TOMCAT)
            notifyAlloc(bucket, ci.getNickName(), userId);
        
        return bucket;
    }
    
    public String getHash() throws RemoteException {
        if(SHOULD_NOTIFY_TOMCAT)
            return this.hashToBreak;
        else
            return "dummy";
        
    }

    public String getId(String nickname, String hostIP, int hostPort) throws RemoteException {
        // Add the new client to the list of already joined clients
        if(attackInProgress) {
            prompt(nickname + "joined. IP=" + hostIP + "PORT=" + hostPort);
            ClientInfo ci = new ClientInfo(nickname, hostIP, hostPort);
            clients.put(ci.getId(), ci);

            if(!guardActive) {
            	guardActive = true;
            	new Guardian(this, clients, mutex).start();
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
    
    public void guardianTerminate() {
    	guardActive = false;
    }
}