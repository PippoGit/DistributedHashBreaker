/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unipi.ce.cds.dhbserver;

import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author filipposcotto
 */
@ServerEndpoint("/status")
public class StatusEndPoint {

    @OnMessage
    public String onMessage(String message) {
        return null;
    }
    
}
