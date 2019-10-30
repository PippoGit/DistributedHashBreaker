package unipi.ce.cds.dhbserver;

import java.io.IOException;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/test_ep/{test_param}")
public class WSEndpoint {
    private volatile String test_param;
    
    @OnOpen
    public void init(@PathParam("test_param") String test_param, Session session) throws IOException {
        this.test_param = test_param;
        System.out.println("Parameter from the client: " + test_param);
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
