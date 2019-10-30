/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unipi.ce.cds.dhbserver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.ServerEndpoint;
import sun.security.util.IOUtils;



@ServerEndpoint("/test_initial_config")
public class TestInitialConfiguration {

    @OnOpen
    public String onOpen() throws IOException{
        String content = new String ( Files.readAllBytes( Paths.get("/Users/filipposcotto/Workspace/DistributedHashBreaker/DHBServer/test_config.json") ) );
        System.out.println(content);
        return content;
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
