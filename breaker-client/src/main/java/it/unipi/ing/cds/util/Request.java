package it.unipi.ing.cds.util;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unipi.ing.cds.parameters.Parameters;

public class Request {
	
	private Hash hasher;
	
	public Request() {
		hasher = new Hash();
	}
	
	public int getBucketNr() {
		return (int) Math.floor(Math.random()*Parameters.NUM_OF_BUCKETS);
	}
	
	public byte[] getTarget() {
    	try {
    		return hasher.getHash("Prova");
    	} catch(NoSuchAlgorithmException nsae) {
    		Logger.getLogger(Parameters.ALGORITHM).log(Level.SEVERE, null, nsae);
    		return null;
    	}
	}
	
}
