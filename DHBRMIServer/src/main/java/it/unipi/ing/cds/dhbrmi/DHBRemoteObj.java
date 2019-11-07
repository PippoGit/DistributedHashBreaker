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

import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class DHBRemoteObj extends UnicastRemoteObject implements DHBRemoteInterface {
    
    // Resources for the RMI Server
    double [] buckets;
    
    public DHBRemoteObj() throws RemoteException {
        super();
        buckets = new double[10];
    }

    @Override
    public double getBucket(String userId) throws RemoteException {
        // The idea is something like this:
        // First the User sends an update to the server. The RMI Server 
        // changes its local data structures and push a notification to the
        // WebServer which should push updates to the Admin through WebSockets
        System.out.println("New request from " + userId);
        
        
        
        
        // Just a test...
        String dest = "ws://localhost:8080/DHBServer/notify";
        final WebSocketContainer client = ContainerProvider.getWebSocketContainer();
        try {
            final Session session = client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {
                    
                    try {
                        session.addMessageHandler(new MessageHandler.Whole<String>() {
                            @Override
                            public void onMessage(String message) {
                                System.out.println("### Received: " + message);
                            }
                        });
                        
                        session.getBasicRemote().sendText(userId);
                    } catch (IOException e) {
                        // do nothing
                    }
                }
                
                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    System.out.println("### Client session closed: " + closeReason);
                }
            }, ClientEndpointConfig.Builder.create().build(), URI.create(dest));
        } catch (DeploymentException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DHBRemoteObj.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        return 42;
    }

    @Override
    public void putStatistics(String test) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getStatistics(String idAttack) throws RemoteException {
        return "Test";
    }    
}
