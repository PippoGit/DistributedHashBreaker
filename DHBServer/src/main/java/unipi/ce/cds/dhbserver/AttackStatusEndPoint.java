/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unipi.ce.cds.dhbserver;

import com.google.gson.Gson;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/attack")
public class AttackStatusEndPoint {
    AttackStatus status;    
    
    @OnMessage
    public String onMessage(String message){
        Gson gson = new Gson();
        status = AttackStatus.getAttackStatus("001");
        return gson.toJson(status);
    }
    
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();
    }

    @OnOpen
    public void onOpen() {
    }
    
}
