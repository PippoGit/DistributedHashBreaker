package it.unipi.ing.cds.parameters;

import java.util.UUID;

public class Parameters {
    
    public static final int BUCKET_BYTES = 4; // LEAST SIGNIFICANT BYTES
    public static final int BUCKET_BITS = 8*BUCKET_BYTES;
    public static final long BUCKET_SIZE = (long)Math.pow(2, BUCKET_BITS);
    
    public static final int MOST_SIGNIFICANT_BYTES = 1;
    public static final int NUM_OF_BUCKETS = 4; //(int)Math.pow(2, 8*MOST_SIGNIFICANT_BYTES);
    
    public static final String ALGORITHM = "SHA-256";
    public static final long SLEEP_TIME = 500;
    
    public static final long GUARD_TIME = 3000;
    
    public static final long STATISTICS_THREAD_SLEEP_TIME = 5000;

    // WEBSOCKET STUFF
    public final static String NOTIFY_ENDPOINT = "ws://localhost:8080/DHBServer/notify";
    
    // ACTIONS FOR WEBSOCKET
    public static final String NACT_BUCKET_COMPLETED = "BUCKET_COMPLETED";
    public static final String NACT_BUCKET_ALLOC     = "BUCKET_ALLOC";
    public static final String NACT_BUCKET_HEARTBEAT = "BUCKET_HEARTBEAT";
    public static final String NACT_PLAN_ATTACK      = "PLAN_ATTACK";
    public static final String NACT_BUCKET_REVOKE    = "BUCKET_REVOKE";
    public static final String NACT_BUCKET_STATS     = "BUCKET_STATS";
    public static final String NACT_STATS_AGGREGATED = "STATS_AGGREGATED";
    
    //RESERVED UUIDs
    public static UUID noAttackPlanned = UUID.fromString("00000000-0000-0000-0000-00000000");
    public static UUID noAvailableBucket = UUID.fromString("1000000-0000-0000-0000-00000000");
    public static UUID remoteError = UUID.fromString("2000000-0000-0000-0000-00000000");
    public static UUID connectionError = UUID.fromString("3000000-0000-0000-0000-00000000");


}