package it.unipi.ing.cds.parameters;

public class Parameters {

    public static final int SHRINKED = 32 / 8;
    
    public static final int BUCKET_BYTES = 3; // LEAST SIGNIFICANT BYTES
    public static final int BUCKET_BITS = 8*BUCKET_BYTES;
    public static final long BUCKET_SIZE = (long)Math.pow(2, BUCKET_BITS);
    
    public static final int MOST_SIGNIFICANT_BYTES = 1;
    public static final int NUM_OF_BUCKETS = (int)Math.pow(2, 8*MOST_SIGNIFICANT_BYTES);
    
    public static final String ALGORITHM = "SHA-256";
    public static final long SLEEP_TIME = 500;

}