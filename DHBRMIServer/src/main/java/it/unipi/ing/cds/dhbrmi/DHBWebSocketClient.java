/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbrmi;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

public class DHBWebSocketClient {
    final WebSocketContainer client = ContainerProvider.getWebSocketContainer();
    final Session session;
    
    public void sendText(String msg) throws IOException {
        session.getAsyncRemote().sendText(msg);
    }
    
    private void open(Session session, EndpointConfig EndpointConfig) {
        session.addMessageHandler((MessageHandler.Whole<String>) (String message) -> {
            System.out.println("### Received: " + message);
            System.out.println("### don't know what this is actually: " + EndpointConfig.toString());
        });
    }
    
    private void close(Session session, CloseReason closeReason) {
        System.out.println("### Client session (" + session.getId() + ") closed: " + closeReason);
    }
    
    public DHBWebSocketClient(String endpoint) throws DeploymentException, IOException {
        session = client.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig EndpointConfig) {
                open(session, EndpointConfig);
            }

            @Override
            public void onClose(Session session, CloseReason closeReason) {
                close(session, closeReason);
            }
        }, ClientEndpointConfig.Builder.create().build(), URI.create(endpoint));
    }
}
