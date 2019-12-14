package it.unipi.ing.cds.dhbws.resource;

import com.google.common.util.concurrent.Monitor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.websocket.Session;

public class AttackStatusRes {
    // Parameters
    public static final int  BUCKET_BYTES = 3; // LEAST SIGNIFICANT BYTES
    public static final int  BUCKET_BITS = 8*BUCKET_BYTES;
    public static final long BUCKET_SIZE = (long)Math.pow(2, BUCKET_BITS);
    public static final int  MOST_SIGNIFICANT_BYTES = 1;
    public static final int  NUM_OF_BUCKETS = 4; // (int)Math.pow(2, 8*MOST_SIGNIFICANT_BYTES);
    
    // Callback
    private static List<Session> sessions = Collections.synchronizedList(new ArrayList<Session>());

    // Monitor and stuff
    private transient final Monitor MONITOR; // A Guava Monitor is just a "more powerful ReentrantLock"

    
    // Resources
    private String idAttack;
    private boolean planned;
    
    private double totalPercentage;
    private int numCollisions;
    private long space;
    
    private int numAvailableBuckets;
    private int numWorkingBuckets;
    private int numCompletedBuckets;
    
    private long totalInspected;
    
    BucketRes [] buckets;
    
    /*
    We don't need a SINGLETON anymore, everything will be handled by TomCat Context...
    
    // THREADSAFE SINGLETON IMPLEMENTATION (Initialization-on-demand holder idiom)
    private static class WrapperSingleton {
         static AttackStatusRes INSTANCE = new AttackStatusRes();
    }
    public static AttackStatusRes getAttackStatus() {
        return WrapperSingleton.INSTANCE;
    }
    */

    public AttackStatusRes() {
        MONITOR = new Monitor();
        buckets = new BucketRes[NUM_OF_BUCKETS];
        resetStatus();
    }
    
    private void resetStatus() {
        idAttack            = "";
        totalPercentage     = 0;
        numCollisions       = 0;
        space               = NUM_OF_BUCKETS * BUCKET_SIZE;
        numAvailableBuckets = NUM_OF_BUCKETS;
        numWorkingBuckets   = 0;
        numCompletedBuckets = 0;
        planned             = false;
        totalInspected      = 0;
        
        // GENERATE BUCKETS!
        for(int i = 0; i < NUM_OF_BUCKETS; i++) {
            buckets[i] = new BucketRes(""+i);
        }
    }
    
    public String getIdAttack() {
        return idAttack;
    }

    public double getTotalPercentage() {
        return totalPercentage;
    }

    public int getNumCollisions() {
        return numCollisions;
    }

    public double getSpace() {
        return space;
    }

    public int getNumAvailableBuckets() {
        return numAvailableBuckets;
    }

    public int getNumWorkingBuckets() {
        return numWorkingBuckets;
    }

    public int getNumCompletedBuckets() {
        return numCompletedBuckets;
    }

    public List<Session> getSessions() {
        return sessions;
    }
       
    public boolean isPlanned() {
        return planned;
    }
    
    public long getTotalInspected() {
        long i = 0;
        for(BucketRes b:buckets) {
            i += b.getInspecetd();
        }
        return i;
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
            buckets[bucket].setLastHeartbeat(new Date(System.currentTimeMillis()));
            numAvailableBuckets--;
            numWorkingBuckets++;
        } finally {
            MONITOR.leave();
        }

    }
    
    public void revokeBucket(int bucket) {
        MONITOR.enter();
        try {
            buckets[bucket].setAvailable(true);
            totalPercentage -= buckets[bucket].getPercentage()/NUM_OF_BUCKETS; // still pretty bad
            buckets[bucket].setPercentage(0);
            numCollisions -= buckets[bucket].getNumCollisions();
            numAvailableBuckets++;
            numWorkingBuckets--;
        } finally {
            MONITOR.leave();
        }
    }
    
    public void completedBucket(int bucket, long inspected, int foundCollisions) {
        MONITOR.enter();
        try {
            buckets[bucket].setDateCompleted(new Date(System.currentTimeMillis()));
            
            // Force percentage to be 100% 
            double oldPercentage = buckets[bucket].getPercentage(); 
            buckets[bucket].setPercentage(100);
            totalPercentage += (100 - oldPercentage)/NUM_OF_BUCKETS;
            
            // Force inspected Update
            buckets[bucket].setInspected(inspected);
            this.totalInspected = getTotalInspected();
            
            // Update found collisions
            numCollisions += foundCollisions;
            buckets[bucket].addCollisions(foundCollisions);
            
            numCompletedBuckets++;
            numWorkingBuckets--;
            
            if(numCompletedBuckets == NUM_OF_BUCKETS) {
                System.out.println("ATTACK COMPLETED!");
                // ...
            }
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
    
    public void updateStatsBucket(int bucket, long inspected, int foundCollisions) {
        MONITOR.enter();
        try {
            numCollisions += foundCollisions;
            buckets[bucket].addCollisions(foundCollisions);
            
            buckets[bucket].setInspected(inspected);
            this.totalInspected = getTotalInspected();

            double percentage = (100*inspected)/BUCKET_SIZE;
            double oldPercentage = buckets[bucket].getPercentage(); 
            buckets[bucket].setPercentage(percentage);
            totalPercentage += (percentage - oldPercentage)/NUM_OF_BUCKETS;
        } finally {
            MONITOR.leave();
        }  
    }
    
    public void updateStatsAggregated(JsonArray buckets) {
        MONITOR.enter();
        try {
            for(JsonElement bb : buckets) {
                JsonObject b = bb.getAsJsonObject();
                int i = b.get("id").getAsInt();
                
                if(this.buckets[i].getPercentage() == 100 || this.buckets[i].isAvailable()) continue; // actualy this should never happen...
                
                
                // Update collisions
                int foundCollisions = b.get("foundCollisions").getAsInt();
                numCollisions += foundCollisions;
                this.buckets[i].addCollisions(foundCollisions);
                
                // Update inspected
                long inspected = b.get("inspected").getAsLong();
                this.buckets[i].setInspected(inspected);
                
                //Update percentage
                double percentage = (100*inspected)/BUCKET_SIZE;
                double oldPercentage = this.buckets[i].getPercentage(); 
                this.buckets[i].setPercentage(percentage);
                totalPercentage += (percentage - oldPercentage)/NUM_OF_BUCKETS;
                
                // Update heartbeat
                this.buckets[i].setLastHeartbeat(new Date(b.get("lastHeartbeat").getAsLong()));
            }
            // Other global stats...
            this.totalInspected = getTotalInspected();         
            
        } finally {
            MONITOR.leave();
        }  
    }
    
    public void planAttack(String id) {
        MONITOR.enter();
        try {
            resetStatus();
            this.idAttack = id;
            this.planned = true;    
        } finally {
            MONITOR.leave();
        }  
    }
}
