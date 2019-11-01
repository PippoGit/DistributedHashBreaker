/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unipi.ce.cds.dhbserver;

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
        this.totalPercentage     = 40;
        this.numCollisions       = 1234;
        this.etc                 = "2h 30m";
        this.numAvailableBuckets = 10;
        this.numWorkingBuckets   = 20;
        this.numCompletedBuckets = 30;
        
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
    
}
