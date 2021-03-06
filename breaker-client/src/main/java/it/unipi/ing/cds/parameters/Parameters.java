package it.unipi.ing.cds.parameters;

import java.rmi.registry.Registry;
import java.util.UUID;

public class Parameters {
    
    public static final int BUCKET_BYTES = 4; // LEAST SIGNIFICANT BYTES
    public static final int BUCKET_BITS = 8*BUCKET_BYTES;
    public static final long BUCKET_SIZE = (long)Math.pow(2, BUCKET_BITS);
    
    public static final int MOST_SIGNIFICANT_BYTES = 1;
    public static final int NUM_OF_BUCKETS = 4; //(int)Math.pow(2, 8*MOST_SIGNIFICANT_BYTES);
    
    public static final String ALGORITHM = "SHA-256";
    public static final long SLEEP_TIME = 500;
    public static final int CYCLES = 3; // Define the number of sleeps before sending updates to the server


    // CLIENTE
    public static final int MYREGISTRY_CLIENT_PORT = Registry.REGISTRY_PORT;//i.e., 1099
    public static final String MYREGISTRY_CLIENT_HOST = "127.0.0.1";
    public static final String DHBRMI_CLIENT_URL = "//" + MYREGISTRY_CLIENT_HOST + ":" + Integer.toString(MYREGISTRY_CLIENT_PORT) + "/DHBServer";
    
    // SERVER
    public static final int MYREGISTRY_PORT = Registry.REGISTRY_PORT;//i.e., 1099
    public static final String MYREGISTRY_HOST = "127.0.0.1"; 
    public static final String DHBRMIURL = "//" + MYREGISTRY_HOST + ":" + Integer.toString(MYREGISTRY_PORT) + "/DHBServer";
    
    //RESERVED UUIDs
	public static UUID noAttackPlanned = UUID.fromString("00000000-0000-0000-0000-00000000");
	public static UUID noAvailableBucket = UUID.fromString("1000000-0000-0000-0000-00000000");
	public static UUID remoteError = UUID.fromString("2000000-0000-0000-0000-00000000");
	public static UUID connectionError = UUID.fromString("3000000-0000-0000-0000-00000000");

}