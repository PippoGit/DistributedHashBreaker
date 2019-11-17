package it.unipi.ing.cds.dhbws.resource;

import com.google.common.util.concurrent.Monitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.websocket.Session;

public class AttackStatusRes {
    // Parameters
    private static final int BUCKET_BYTES = 4; // LEAST SIGNIFICANT BYTES
    private static final int BUCKET_BITS = 8*BUCKET_BYTES;
    private static final long BUCKET_SIZE = (long)Math.pow(2, BUCKET_BITS);
    private static final int MOST_SIGNIFICANT_BYTES = 1;
    private static final int NUM_OF_BUCKETS = (int)Math.pow(2, 8*MOST_SIGNIFICANT_BYTES);
    
    // Callback
    private static List<Session> sessions = Collections.synchronizedList(new ArrayList<Session>());
    
    private final Monitor MONITOR = new Monitor(); // A Guava Monitor is just a "more powerful ReentrantLock"
       
    // Resources
    private String idAttack;
    private boolean planned;
    
    private double totalPercentage;
    private int numCollisions;
    private String etc;
    
    private int numAvailableBuckets;
    private int numWorkingBuckets;
    private int numCompletedBuckets;
    
    BucketRes [] buckets;
    
    // THREADSAFE SINGLETON IMPLEMENTATION (Initialization-on-demand holder idiom)
    private static class WrapperSingleton {
         static AttackStatusRes INSTANCE = new AttackStatusRes();
     }
    public static AttackStatusRes getAttackStatus() {
        return WrapperSingleton.INSTANCE;
    }
    
    
    // The constructor will be called just ONCE and by the JVM, so no multithread
    // issues here...
    private AttackStatusRes() {
        buckets = new BucketRes[NUM_OF_BUCKETS];

        this.idAttack            = "";
        this.totalPercentage     = 0;
        this.numCollisions       = 0;
        this.etc                 = "tbd";
        this.numAvailableBuckets = NUM_OF_BUCKETS;
        this.numWorkingBuckets   = 0;
        this.numCompletedBuckets = 0;
        this.planned             = false;
        
        // GENERATE BUCKETS!
        for(int i = 0; i < NUM_OF_BUCKETS; i++) {
            buckets[i] = new BucketRes(""+i);
        }
    }
    
    public String getIdAttack() {
        return idAttack;
    }

    public void setIdAttack(String idAttack) {
        this.idAttack = idAttack;
    }

    public double getTotalPercentage() {
        return totalPercentage;
    }

    public void setTotalPercentage(double totalPercentage) {
        this.totalPercentage = totalPercentage;
    }

    public int getNumCollisions() {
        return numCollisions;
    }

    public void setNumCollisions(int numCollisions) {
        this.numCollisions = numCollisions;
    }

    public String getEtc() {
        return etc;
    }

    public void setEtc(String etc) {
        this.etc = etc;
    }

    public int getNumAvailableBuckets() {
        return numAvailableBuckets;
    }

    public void setNumAvailableBuckets(int numAvailableBuckets) {
        this.numAvailableBuckets = numAvailableBuckets;
    }

    public int getNumWorkingBuckets() {
        return numWorkingBuckets;
    }

    public void setNumWorkingBuckets(int numWorkingBuckets) {
        this.numWorkingBuckets = numWorkingBuckets;
    }

    public int getNumCompletedBuckets() {
        return numCompletedBuckets;
    }

    public void setNumCompletedBuckets(int numCompletedBuckets) {
        this.numCompletedBuckets = numCompletedBuckets;
    }

    public BucketRes[] getBuckets() {
        return buckets;
    }

    public void setBuckets(BucketRes[] buckets) {
        this.buckets = buckets;
    }
    
    public List<Session> getSessions() {
        return sessions;
    }
       
    public boolean isPlanned() {
        return planned;
    }

    public void setPlanned(boolean planned) {
        this.planned = planned;
    }
    
    
    // CRITICAL stuff here!
    public void allocBucket(int bucket, String worker, String uuid) {
        MONITOR.enter();
        try {
            // do things while occupying the monitor
            buckets[bucket].setAvailable(false);
            buckets[bucket].setDateAllocation(new Date(System.currentTimeMillis()));
            buckets[bucket].setWorkerNickname(worker);
            buckets[bucket].setUUIDWorker(uuid);
            this.numAvailableBuckets--;
            this.numWorkingBuckets++;
        } finally {
            MONITOR.leave();
        }

    }
    
    public void revokeBucket(int bucket) {
        MONITOR.enter();
        try {
            buckets[bucket].setAvailable(true);
            buckets[bucket].setPercentage(0);

            this.numCollisions -= buckets[bucket].getNumCollisions();
            this.numAvailableBuckets++;
            this.numWorkingBuckets--;
        } finally {
            MONITOR.leave();
        }
    }
    
    public void completedBucket(int bucket) {
        MONITOR.enter();
        try {
            buckets[bucket].setDateCompleted(new Date(System.currentTimeMillis()));
            this.numCompletedBuckets++;
            this.numWorkingBuckets--;
            this.totalPercentage = 100*this.numCompletedBuckets/NUM_OF_BUCKETS;
        } finally {
            MONITOR.leave();
        }        
    }
    
    public void beatBucket(int bucket) {
        MONITOR.enter();
        try {
            buckets[bucket].setLastHeartbeat(new Date(System.currentTimeMillis()));
        } finally {
            MONITOR.leave();
        }  
    }
    
    
    public void progressBucket(int bucket, double percentage) {
        MONITOR.enter();
        try {
            buckets[bucket].setPercentage(percentage);
        } finally {
            MONITOR.leave();
        }             
    }
    
    public void updateStatsBucket(int bucket, double percentage, int foundCollisions) {
        MONITOR.enter();
        try {
            setNumCollisions(numCollisions + foundCollisions);
            buckets[bucket].addCollisions(foundCollisions);
            progressBucket(bucket, percentage);
        } finally {
            MONITOR.leave();
        }  
    }
    
    public void planAttack(String id) {
        MONITOR.enter();
        try {
            setIdAttack(id);
            setPlanned(true);    
        } finally {
            MONITOR.leave();
        }  
    }
}
