/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbws.resource;

public class AttackStatus {
    
    //Singleton
    private static AttackStatus _instance;
    private static final int NUM_BUCKETS = 30;
    
    private String idAttack; // maybe useless?
    
    private double totalPercentage;
    private int numCollisions;
    private String etc;
    
    private int numAvailableBuckets;
    private int numWorkingBuckets;
    private int numCompletedBuckets;
    
    Bucket [] buckets;
       
    public AttackStatus(String idAttack) {
        this.idAttack = idAttack;
        buckets = new Bucket[NUM_BUCKETS];

        
        // Quick test...
        this.totalPercentage     = 45;
        this.numCollisions       = 1234;
        this.etc                 = "2h 27m";
        this.numAvailableBuckets = 10;
        this.numWorkingBuckets   = 20;
        this.numCompletedBuckets = 15;
        
        // GENERATE RANDOM BUCKETS!
        for(int i = 0; i < NUM_BUCKETS; i++) {
            buckets[i] = new Bucket(""+i, true);
        }
    }
    
    public static AttackStatus getAttackStatus(String idAttack) {
        if(_instance == null) {
            _instance = new AttackStatus(idAttack);
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

    public Bucket[] getBuckets() {
        return buckets;
    }

    public void setBuckets(Bucket[] buckets) {
        this.buckets = buckets;
    }
    
}
