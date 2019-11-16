package it.unipi.ing.cds.parameters;

public class Parameters {

    public static final int SHRINKED = 32 / 8;
    
    public static final int BUCKET_BYTES = 4; // LEAST SIGNIFICANT BYTES
    public static final int BUCKET_BITS = 8*BUCKET_BYTES;
    public static final long BUCKET_SIZE = (long)Math.pow(2, BUCKET_BITS);
    
    public static final int MOST_SIGNIFICANT_BYTES = 1;
    public static final int NUM_OF_BUCKETS = (int)Math.pow(2, 8*MOST_SIGNIFICANT_BYTES);
    
    public static final String ALGORITHM = "SHA-256";
    public static final long SLEEP_TIME = 500;
    
    public static final long GUARD_TIME = 3000;

    
    // WEBSOCKET STUFF
    public final static String NOTIFY_ENDPOINT = "ws://localhost:8080/DHBServer/notify";

    
    // ACTIONS FOR WEBSOCKET
    public static final String NACT_BUCKET_COMPLETED = "BUCKET_COMPLETED";
    public static final String NACT_BUCKET_ALLOC     = "BUCKET_ALLOC";
    public static final String NACT_BUCKET_HEARTBEAT = "BUCKET_HEARTBEAT";
    public static final String NACT_PLAN_ATTACK      = "PLAN_ATTACK";
    public static final String NACT_BUCKET_REVOKE    = "BUCKET_REVOKE";
    public static final String NACT_BUCKET_STATS     = "BUCKET_STATS";


}