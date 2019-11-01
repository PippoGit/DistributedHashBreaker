/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unipi.ce.cds.dhbserver;

public class AttackStatus {
    
    //Singleton
    private static AttackStatus _instance;
    
    private String idAttack;
    private int numBuckets;
    
    private double totalPercentage;
    private int numCollisions;
    private String etc;
    
    private int numAvailableBuckets;
    private int numWorkingBuckets;
    private int numCompletedBuckets;
    
    Bucket [] buckets;
       
    public AttackStatus(String idAttack, int numBuckets) {
        this.idAttack = idAttack;
        this.numBuckets = numBuckets;
        buckets = new Bucket[numBuckets];

        
        // Quick test...
        this.totalPercentage     = 40;
        this.numCollisions       = 1234;
        this.etc                 = "2h 30m";
        this.numAvailableBuckets = 10;
        this.numWorkingBuckets   = 20;
        this.numCompletedBuckets = 30;
        randomBuckets();
    }
    
    public void randomBuckets() {
        for(int i = 0; i < numBuckets; i++) {
            buckets[i] = new Bucket(""+i, true);
        }
    }
    
    public static AttackStatus getAttackStatus(String idAttack) {
        if(_instance == null) {
            System.out.println("Costruisco un oggetto status...");
            _instance = new AttackStatus(idAttack, 20);
        }
        
        System.out.println("Fornisco lo status...");
        return _instance;
    }
    
}
