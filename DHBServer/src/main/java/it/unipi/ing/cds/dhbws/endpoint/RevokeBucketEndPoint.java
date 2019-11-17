/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbws.endpoint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unipi.ing.cds.dhbrmi.server.iface.DHBRemoteServerInterface;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/revoke")
public class RevokeBucketEndPoint {
    private static final int MYREGISTRY_PORT = Registry.REGISTRY_PORT;//i.e., 1099
    private static final String MYREGISTRY_HOST = "127.0.0.1";
    private static final String DHBRMIURL = "//" + MYREGISTRY_HOST + ":" + Integer.toString(MYREGISTRY_PORT) + "/DHBServer";
    
    @OnMessage
    public void onMessage(String message) {
        System.out.println("Admin required to revoke a bucket ... " + message);
        JsonElement jsonElement = new JsonParser().parse(message);        
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String uuid = jsonObject.get("uuid").getAsString();
        System.out.println("the uuid was " + uuid);

        try { 
            DHBRemoteServerInterface server = (DHBRemoteServerInterface) Naming.lookup(DHBRMIURL);
            server.leave(uuid);
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            Logger.getLogger(PlanEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }    
    }
}
