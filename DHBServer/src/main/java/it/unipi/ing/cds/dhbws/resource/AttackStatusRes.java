/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.


TRY THIS:

public class MySingleton {

     private static class MyWrapper {
         static MySingleton INSTANCE = new MySingleton();
     }

     private MySingleton () {}

     public static MySingleton getInstance() {
         return MyWrapper.INSTANCE;
     }
}


 */
package it.unipi.ing.cds.dhbws.resource;

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
    
    // Singleton
    private static AttackStatusRes _instance;
   
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
       
    public AttackStatusRes() {
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
    
    public static AttackStatusRes getAttackStatus() {
        if(_instance == null) {
            _instance = new AttackStatusRes();
        }
        return _instance;
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
    
    public void allocBucket(int bucketId, String worker) {
        buckets[bucketId].setAvailable(false);
        buckets[bucketId].setDateAllocation(new Date(System.currentTimeMillis()));
        buckets[bucketId].setIdWorker(worker);
        this.numAvailableBuckets--;
        this.numWorkingBuckets++;
    }
    
    public void revokeBucket(int bucketId) {
        buckets[bucketId].setAvailable(true);

        this.numAvailableBuckets++;
        this.numWorkingBuckets--;
    }
    
    public void completedBucket(int bucketId) {
        buckets[bucketId].setDateCompleted(new Date(System.currentTimeMillis()));
        this.numCompletedBuckets++;
        this.numWorkingBuckets--;
        this.totalPercentage = 100*this.numCompletedBuckets/NUM_OF_BUCKETS;
    }
    
    public void beatBucket(int bucketId) {
        buckets[bucketId].setLastHeartbeat(new Date(System.currentTimeMillis()));
    }
    
    
    public void progressBucket(int bucketId, double percentage) {
        buckets[bucketId].setPercentage(percentage);
    }
    
    public void updateStatsBucket(int bucketId, double percentage, int foundCollisions) {
        setNumCollisions(numCollisions + foundCollisions);
        progressBucket(bucketId, percentage);
    }
    
    public void planAttack(String id) {
        setIdAttack(id);
        setPlanned(true);    
    }
}
