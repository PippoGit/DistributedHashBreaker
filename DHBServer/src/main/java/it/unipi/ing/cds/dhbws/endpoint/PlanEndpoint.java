/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbws.endpoint;

import javax.websocket.server.ServerEndpoint;
import it.unipi.ing.cds.dhbrmi.iface.DHBRemoteInterface;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnOpen;
import javax.websocket.Session;

@ServerEndpoint("/plan")
public class PlanEndpoint {
    private static final int MYREGISTRY_PORT = Registry.REGISTRY_PORT;//i.e., 1099
    private static final String MYREGISTRY_HOST = "127.0.0.1";
    private static final String DHBRMIURL = "//" + MYREGISTRY_HOST + ":" + Integer.toString(MYREGISTRY_PORT) + "/DHBServer";

    @OnOpen
    public void onOpen(Session session) {
        String idAttack;
        System.out.println("Requested attack... ");

        try { 
            DHBRemoteInterface server = (DHBRemoteInterface) Naming.lookup(DHBRMIURL);
            idAttack = server.planAttack("TESTHASHTOBREAK");
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            Logger.getLogger(PlanEndpoint.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
