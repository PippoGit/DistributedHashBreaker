/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbws.endpoint;
import it.unipi.ing.cds.dhbws.resource.AttackStatus;

import com.google.gson.Gson;
import it.unipi.ing.cds.dhbrmi.iface.DHBRemoteInterface;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    final private AttackStatus status = AttackStatus.getAttackStatus("001");
    List<Session> sessions = Collections.synchronizedList(new ArrayList<Session>());
    
    private void broadcast() throws IOException {
        final Gson gson = new Gson();

        for(Session s: sessions) {
            s.getBasicRemote().sendText(gson.toJson(status));
        }
            
    }
    
    @OnMessage
    public String onMessage(final Session session, String message){
        final Gson gson = new Gson();      
        return gson.toJson(status);
    }
    
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();        
    }

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("New Connection to the Dashboard!");
        try {
            final Gson gson = new Gson();
            session.getBasicRemote().sendText(gson.toJson(status));
        } catch (IOException ex) {
            Logger.getLogger(AttackStatusEndPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        sessions.add(session);
        
        
        // THIS IS JUST A TEST FOR REAL TIME UPDATES!!!!!!!
        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                status.setTotalPercentage(status.getTotalPercentage()+10);
                if(status.getTotalPercentage() >= 100) {
                    status.setTotalPercentage(100);
                    scheduler.shutdown();
                }
                try {
                    broadcast();
                } catch (IOException ex) {
                    Logger.getLogger(AttackStatusEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        scheduler.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
        ///////////////////////////////////////////////////
    
    
        // TEST
        String DHBRMIURL = "//" + "127.0.0.1" + ":" + "1099" + "/DHBServer";

        System.out.println("Testing RMI...");
        DHBRemoteInterface server;
        try {
            server = (DHBRemoteInterface) Naming.lookup(DHBRMIURL);
            String diom = (server.getStatistics("001"));
            for(Session s: sessions) {
                        s.getBasicRemote().sendText(diom);
                    }
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            Logger.getLogger(AttackStatusEndPoint.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AttackStatusEndPoint.class.getName()).log(Level.SEVERE, null, ex);
        }
        ////
        

            
            
        ////
    
    
    }
    
    @OnClose
    public void onClose(Session session) {
        System.out.println("Connection closed.");
        sessions.remove(session);
    }
    
}
