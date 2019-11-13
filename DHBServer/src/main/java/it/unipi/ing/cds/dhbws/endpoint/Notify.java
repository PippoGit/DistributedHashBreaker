/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbws.endpoint;

import com.google.gson.Gson;
import it.unipi.ing.cds.dhbws.resource.AttackStatusRes;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author filipposcotto
 */
@ServerEndpoint("/notify")
public class Notify {
    final private AttackStatusRes status = AttackStatusRes.getAttackStatus();
    
    private void broadcast() throws IOException {
        final Gson gson = new Gson();
        for(Session s: status.getSessions()) {
            s.getBasicRemote().sendText(gson.toJson(status));
        }
    }
    
    @OnMessage
    public String onMessage(String message) {
        System.out.println("New notification!");
        try {
            // Update Status
            status.setFromJson(message);
            // Broadcast to dashboardsss
            broadcast();
        } catch (IOException ex) {
            Logger.getLogger(Notify.class.getName()).log(Level.SEVERE, null, ex);
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
