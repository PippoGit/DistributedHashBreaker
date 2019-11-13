package it.unipi.ing.cds.util;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unipi.ing.cds.parameters.Parameters;

import it.unipi.ing.cds.dhbrmi.iface.DHBRemoteInterface;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public class Request {
	
	private Hash hasher;
	private String nickname;
        
        // TEST
        private static final int MYREGISTRY_PORT = Registry.REGISTRY_PORT;//i.e., 1099
        private static final String MYREGISTRY_HOST = "127.0.0.1";
	////
        
	public Request(String nickname) {
		hasher = new Hash();
		this.nickname = nickname;
		System.out.println(this.nickname + " joined");
	}
	
	public int getBucketNr() {
            // TEST
            String DHBRMIURL = "//" + MYREGISTRY_HOST + ":" + Integer.toString(MYREGISTRY_PORT) + "/DHBServer";            
            try { 
                System.out.println("Testing RMI...");
                DHBRemoteInterface server = (DHBRemoteInterface) Naming.lookup(DHBRMIURL);
                double bucket = server.getBucket(nickname);
                System.out.println("Received " + bucket);
                
            } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
            }
            ////
            
            return (int) Math.floor(Math.random()*Parameters.NUM_OF_BUCKETS);
	}
	
	public byte[] getTarget() {
            try {
                // TEST
                String DHBRMIURL = "//" + MYREGISTRY_HOST + ":" + Integer.toString(MYREGISTRY_PORT) + "/DHBServer";
                String hash = "Prova";

                try {
                    System.out.println("Testing RMI...");
                    DHBRemoteInterface server = (DHBRemoteInterface) Naming.lookup(DHBRMIURL);
                    hash = server.getHash();
                    System.out.println("The hash to break is: " + hash);

                } catch (NotBoundException | MalformedURLException | RemoteException ex) {
                    Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
                }
                ////
                
                return hasher.getHash(hash);
            } catch(NoSuchAlgorithmException nsae) {
                Logger.getLogger(Parameters.ALGORITHM).log(Level.SEVERE, null, nsae);
                return null;
            }
	}
	
}
