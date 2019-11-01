/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unipi.ce.cds.dhbserver;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/bucket")
public class BucketStatus {
    AttackStatus status;
    
    @OnMessage
    public String onMessage(Session session, String message){
        status = AttackStatus.getAttackStatus("001");
        
        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
        System.out.println(jsonObject.get("id").getAsString()); //John
        
        String id_ = jsonObject.get("id").getAsString();
        
        Gson gson = new Gson();
        System.out.println(gson.toJson(status));
        
        return status.buckets[Integer.parseInt(id_)].JSONStringify();
    }
    
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();
    }
    
    
}
