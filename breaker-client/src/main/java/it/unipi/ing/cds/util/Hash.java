package it.unipi.ing.cds.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unipi.ing.cds.parameters.Parameters;

public class Hash {
	
    public byte[] getHash(byte[] plaintext) throws NoSuchAlgorithmException {
    	try {
    		MessageDigest md = MessageDigest.getInstance(Parameters.ALGORITHM);
    		md.update(plaintext);
    		byte byteData[] = md.digest();
    		return byteData;
    	} catch(NoSuchAlgorithmException nsae) {
    		Logger.getLogger(Parameters.ALGORITHM).log(Level.SEVERE, null, nsae);
    		return null;
    	}
    }
    
    public byte[] getHash(String plaintext) throws NoSuchAlgorithmException {
    	try {
    		MessageDigest md = MessageDigest.getInstance(Parameters.ALGORITHM);
    		md.update(plaintext.getBytes());
    		byte byteData[] = md.digest();
    		return byteData;
    	} catch(NoSuchAlgorithmException nsae) {
    		Logger.getLogger(Parameters.ALGORITHM).log(Level.SEVERE, null, nsae);
    		return null;
    	}
    }

}
