/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbrmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import it.unipi.ing.cds.dhbrmi.server.iface.DHBRemoteServerInterface;

public class DHBServer {
    //the default port for rmiregistry is 1099;
    //instead, by default connections to remote objects are on random ports
    //(otherwise specified in the export command)
    private static final int MYREGISTRY_PORT = Registry.REGISTRY_PORT;//i.e., 1099
    //the "myRegistry" reference variable isn't actually used; 
    //for the sake of clarity, we just assign it the value returned 
    //by the registry creation method.
    private static Registry myRegistry;
    private static final String MYREGISTRY_HOST = "127.0.0.1";
    private static String servantName;

    public DHBServer(){
        this("DHBServer"); //default name
    }
    public DHBServer(String servantName) {
        DHBServer.servantName = servantName;
        try {
            //a servant object is created
            DHBRemoteServerInterface c = new DHBRemoteObj();
            //((DHBRemoteObj)c).initState(); // RIMUOVERREEEEEE
            //next: binding to the rmiregistry
            Naming.rebind("//"+MYREGISTRY_HOST+":"+Integer.toString(MYREGISTRY_PORT)+"/"+DHBServer.servantName, c);

            //alternative way, which is mandatory in case CalcRemoteObject 
            //would not extend UnicastRemoteObject, as it actually does.
//            try {
//                CalcRemoteInterface calcStub = 
//                        (CalcRemoteInterface)UnicastRemoteObject.exportObject(c, 0); // 0 means: default
//                myRegistry.rebind(servantName, calcStub);
//            } catch (ExportException e) {
//                System.err.println("Outch! Servant \""+servantName+"\" has already been exported!\n");
//                myRegistry.rebind(servantName, c);
//            }
            
        } catch (Exception e) { 
            System.err.println("Trouble: " + e);
            e.printStackTrace();
        }
    }
  
    public static void startRegistry(int port) throws RemoteException{

        myRegistry = LocateRegistry.createRegistry(port);
        System.out.println("Registry created - port "+Integer.toString(port));
        
        //be careful in using LocateRegistry.createRegistry(..):
        //such a method returns a local reference, but no connection to the 
        //registry is actually created. Thus, in case of problems, they will
        //appear later on.
    }

    
    public static void main(String[] args) throws Exception {
        // Remember:
        // RMI's class loader will download classes from remote locations 
        // only if a security manager has been set.
        if (System.getSecurityManager() == null) {
            // java.lang.SecurityManager 
            System.setProperty("java.security.policy","file:src/security.policy"); // I think this is required for macos (?)
            System.setSecurityManager(new SecurityManager());
        }
        
        startRegistry(MYREGISTRY_PORT);
                       
        //the server is set up
        DHBServer dhbServer = new DHBServer();
    }
}
