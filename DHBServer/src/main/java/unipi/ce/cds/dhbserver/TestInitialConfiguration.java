/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unipi.ce.cds.dhbserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;


@ServerEndpoint("/test_initial_config")
public class TestInitialConfiguration {
    
    @OnMessage
    public String onMessage(String message){
        System.out.println("Message from the client: " + message);
        String content;
        
        try {
            content = new String ( Files.readAllBytes( Paths.get("/Users/filipposcotto/Workspace/DistributedHashBreaker/DHBServer/test_config.json") ) );
        } catch (IOException ex) {
            Logger.getLogger(TestInitialConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            content = "{ \"error\": \"An error occured...\"}";
        }
        
        return content;
    }
    
    @OnError
    public void onError(Throwable e){
        e.printStackTrace();
    }
    
}
