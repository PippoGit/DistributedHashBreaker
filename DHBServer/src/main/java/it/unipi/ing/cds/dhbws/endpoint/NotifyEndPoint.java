/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbws.endpoint;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unipi.ing.cds.dhbws.resource.AttackStatusRes;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/notify")
public class NotifyEndPoint {
    final private AttackStatusRes status = AttackStatusRes.getAttackStatus();
    
    private void broadcast() throws IOException {
        final Gson gson = new Gson();
        for(Session s: status.getSessions()) {
            s.getBasicRemote().sendText(gson.toJson(status));
        }
    }
    
    private void bucketAlloc(int bucketId, String worker) {
        status.allocBucket(bucketId, worker);
    }
    
    private void bucketRevoke(int bucketId) {
        status.revokeBucket(bucketId);
    }
    
    private void bucketHeartbeat(int bucketId) {
        status.beatBucket(bucketId);
    }
    
    private void bucketCompleted(int bucketId) {
        status.completedBucket(bucketId);
    }
    
    private void bucketStats(int bucketId, double percentage, int foundCollisions) {
        status.setNumCollisions(status.getNumCollisions() + foundCollisions);
        status.progressBucket(bucketId, percentage);
    }
    
    private void planAttack(String id) {
        status.setIdAttack(id);
        status.setPlanned(true);
    }
    
    private void handleAction(String action, JsonObject parameters) {
        switch(action){
            case "BUCKET_ALLOC":
                bucketAlloc(parameters.get("bucketId").getAsInt(), parameters.get("worker").getAsString());
                break;
            case "BUCKET_REVOKE":
                bucketRevoke(parameters.get("bucketId").getAsInt());
                break;
            case "BUCKET_COMPLETED":
                bucketCompleted(parameters.get("bucketId").getAsInt());
                break;
            case "BUCKET_HEARTBEAT":
                bucketHeartbeat(parameters.get("bucketId").getAsInt());
                break;
            case "BUCKET_STATS":
                bucketStats(parameters.get("bucketId").getAsInt(), parameters.get("percentage").getAsDouble(), parameters.get("foundCollisions").getAsInt());
                break;
            
            case "PLAN_ATTACK":
                planAttack(parameters.get("idAttack").getAsString());
                break;
        }
    }
    
    @OnMessage
    public String onMessage(String message) {
        System.out.println(" +++ [NOTIFICATION] " + message);
               
        try {
            JsonElement jsonElement = new JsonParser().parse(message);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String action = jsonObject.get("action").getAsString();
            handleAction(action, jsonObject.get("params").getAsJsonObject());
            broadcast();
        } catch (IOException ex) {
            Logger.getLogger(NotifyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "200";
    }
    
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();        
    }
    
    @OnClose
    public void onClose(Session session) {
        System.out.println("Connection closed.");
    }
    
}
