/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbws.endpoint;
import it.unipi.ing.cds.dhbws.resource.AttackStatusRes;

import com.google.gson.Gson;
import java.io.IOException;

// Timeout stuff...
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/attack")
public class AttackStatusEndPoint {
    final private AttackStatusRes status = AttackStatusRes.getAttackStatus();
    String currentAttack;
    
    
    private void sendStatus(Session session) {
        try {
            final Gson gson = new Gson();
            if(status.isPlanned()) {
                session.getBasicRemote().sendText(gson.toJson(status));
            }
            else {
                session.getBasicRemote().sendText("{\"error\": \"No attack is planned.\"}");
            }
        } catch (IOException ex) {
            Logger.getLogger(AttackStatusEndPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @OnMessage
    public String onMessage(final Session session, String message){
        // I don't know if i actually need this method here...
        return "200";
    }
    
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();        
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New Connection to the Dashboard!");
        sendStatus(session);
        status.getSessions().add(session);   
    }
    
    @OnClose
    public void onClose(Session session) {
        System.out.println("Connection closed.");
        status.getSessions().remove(session);
    }
    
}
