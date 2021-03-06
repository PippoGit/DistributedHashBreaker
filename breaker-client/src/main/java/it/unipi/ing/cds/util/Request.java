package it.unipi.ing.cds.util;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unipi.ing.cds.dhbrmi.server.iface.DHBRemoteServerInterface;
import it.unipi.ing.cds.parameters.Parameters;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Request {
	
	private String nickname;
	private DHBRemoteServerInterface server;
	private String ID;
	
	private static Request istance=null;

	public static Request getInstance(String nickname) {
        if(istance==null)
	    	istance = new Request(nickname);
	    return istance;
	}    
	
	public static Request getInstance() {
	    return istance;
	}  
	private Request(String nickname) {
        this.nickname = nickname;
        try {
            server = (DHBRemoteServerInterface) Naming.lookup(Parameters.DHBRMIURL);
            System.out.println(Parameters.DHBRMIURL);
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            e.printStackTrace();
        }
        
        System.out.println(this.nickname + " joined");
	}
	
	public void prompt(String s) {
		System.out.println("[CLIENT-REQUEST] " + s);
	}
	
	public String getId(String nickname, String hostIP, int hostPort) throws MalformedURLException {
        try { 
            System.out.println("Testing RMI...");
            this.ID = server.getId(nickname, hostIP, hostPort);
            prompt("ID=" + this.ID);
            return this.ID;
            
        } catch (RemoteException ex) {
        	prompt("ciao");
	       ex.printStackTrace();
	        return Parameters.remoteError.toString();
	    }
	}
	
    public int getBucketNr() throws MalformedURLException {
        // TEST
        int bucket = 0;

        try { 
            System.out.println("Testing RMI...");
            bucket = server.getBucket(ID);
            System.out.println("Received " + bucket);

        } catch (RemoteException ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        }
        ////
        return bucket;
    }
    
    public void sendStatistics(ArrayList<byte[]> partialCollisions, long inspected) throws MalformedURLException {
        // TEST
        try { 
            prompt("Sending Statistics...(partial inspected " + inspected +") with ID " + ID);
            server.sendStatistics(partialCollisions, inspected, ID);
        } catch (RemoteException ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] getTarget() {
        try {
            // TEST
            String hash = "Prova";

            try {
                System.out.println("Testing RMI...");
                hash = server.getHash();
                System.out.println("The hash to break is: " + hash);

            } catch (RemoteException ex) {
                Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return Hash.getHash(hash);
        } catch(NoSuchAlgorithmException nsae) {
            Logger.getLogger(Parameters.ALGORITHM).log(Level.SEVERE, null, nsae);
            return null;
        }
    }
    
    public void leave() {
        try {
            if(server.leave(ID)) {
                prompt("User " + nickname + " leaved successfully");
            }
            else {
                prompt("Something went wrong in leave function (user " + nickname + ")");
            }
        } catch (RemoteException e) {
                e.printStackTrace();
        }
    }
}