/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbws.endpoint;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
    
    private void handleAction(String action, JsonObject parameters) {
        switch(action){
            case "BUCKET_ALLOC":
                status.allocBucket(parameters.get("bucket").getAsInt(), parameters.get("worker").getAsString(), parameters.get("UUIDWorker").getAsString());
                break;
            case "BUCKET_REVOKE":
                status.revokeBucket(parameters.get("bucket").getAsInt());
                break;
            case "BUCKET_COMPLETED":
                status.completedBucket(parameters.get("bucket").getAsInt());
                break;
            case "BUCKET_HEARTBEAT":
                status.beatBucket(parameters.get("bucket").getAsInt());
                break;
            case "BUCKET_STATS":
                status.updateStatsBucket(parameters.get("bucket").getAsInt(), parameters.get("percentage").getAsDouble(), parameters.get("foundCollisions").getAsInt());
                break;
            
            case "PLAN_ATTACK":
                status.planAttack(parameters.get("attack").getAsString());
                break;
        }
    }
    
    @OnMessage
    public String onMessage(String message) {
        System.out.println(" +++ [NOTIFICATION] " + message);
               
        try {
            JsonElement jsonElement = new JsonParser().parse(message);
            JsonArray request = jsonElement.getAsJsonArray();
            String action = request.get(0).getAsString();
            
            // Update state
            handleAction(action, request.get(1).getAsJsonObject());
            
            // Push realtime upd
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