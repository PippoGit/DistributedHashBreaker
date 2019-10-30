package unipi.ce.cds.dhbserver;

import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/endpoint2")
public class WSEndpoint {

    @OnOpen
    public void onOpen(){
        System.out.println("Open Connection ...");
    }
    
    @OnMessage
    public String onMessage(String message){
        System.out.println("Message from the client: " + message);
        String echoMsg = "Echo from the server : " + message;
        return echoMsg;
    }
    
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();
    }
    
}
