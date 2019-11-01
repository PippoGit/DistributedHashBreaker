/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unipi.ce.cds.dhbserver;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/bucket")
public class BucketStatus {

    @OnMessage
    public String onMessage(Session session, String message){
        
        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
        System.out.println(jsonObject.get("id").getAsString()); //John
        
        String id_ = jsonObject.get("id").getAsString();
        
        String content = "" +
        "{"+ 
        "    \"id\"             : \"" + id_ + "\", "        +
        "    \"percentage\"     : " + Math.floor(Math.random()*100) + ",\n"           +
        "    \"idWorker\"       : \"username\","            +
        "    \"dateAllocation\" : \"2016-07-24 19:20:13\"," +
        "    \"lastHeartbeat\"  : \"2016-07-25 00:00:00\"," +
        "    \"available\"      : false"                    +
        "}";        
        return content;
    }
    
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();
    }
    
    
}
