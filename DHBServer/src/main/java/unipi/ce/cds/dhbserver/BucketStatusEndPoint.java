/*


THIS CLASS IS ACTUALLY USELESS!!!!!!!!!!!!
ONE ENDPOINT IS ENOUGH!


*/
package unipi.ce.cds.dhbserver;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/bucket")
public class BucketStatusEndPoint {
    AttackStatus status;
    
    @OnMessage
    public String onMessage(Session session, String message){
        status = AttackStatus.getAttackStatus("001");
        
        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();        
        int id_ = Integer.parseInt(jsonObject.get("id").getAsString());
        
        return status.buckets[id_].JSONStringify();
    }
    
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();
    }
    
    
}
