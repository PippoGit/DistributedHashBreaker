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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/notify")
public class NotifyEndPoint {
   
    private AttackStatusRes status; // status = AttackStatusRes.getAttackStatus();
    
    private void broadcast() throws IOException {
        final Gson gson = new Gson();
        for(Session s: status.getSessions()) {
            s.getAsyncRemote().sendText(gson.toJson(status));
        }
    }
    
    private void handleAction(String action, JsonObject parameters) throws IOException {
        switch(action){
            case "BUCKET_ALLOC":
                status.allocBucket(parameters.get("bucket").getAsInt(), parameters.get("worker").getAsString(), parameters.get("UUIDWorker").getAsString());
                broadcast();
                break;
            case "BUCKET_REVOKE":
                status.revokeBucket(parameters.get("bucket").getAsInt());
                broadcast();
                break;
            case "BUCKET_COMPLETED":
                status.completedBucket(parameters.get("bucket").getAsInt(), parameters.get("inspected").getAsLong(), parameters.get("foundCollisions").getAsInt());
                broadcast();
                break;
            case "BUCKET_HEARTBEAT":
                status.beatBucket(parameters.get("bucket").getAsInt());
                break;
                
            case "BUCKET_STATS":
                status.updateStatsBucket(parameters.get("bucket").getAsInt(), parameters.get("inspected").getAsLong(), parameters.get("foundCollisions").getAsInt());
                broadcast();
                break;
            
            case "STATS_AGGREGATED":
                status.updateStatsAggregated(parameters.get("buckets").getAsJsonArray());
                broadcast();
                break;
            case "PLAN_ATTACK":
                status.planAttack(parameters.get("attack").getAsString());
                broadcast();
                break;
        }
    }
    
    @OnOpen
    public void onOpen(Session session) {
        try {
            Context initCtx;
            initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            this.status = (AttackStatusRes) envCtx.lookup("bean/AttackStatusRes");
        }
        catch (NamingException ex) {
            Logger.getLogger(NotifyEndPoint.class.getName()).log(Level.SEVERE, null, ex);
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