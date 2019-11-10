/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbws.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.websocket.Session;

public class AttackStatusRes {
    //Callback stuff???
    private static List<Session> sessions = Collections.synchronizedList(new ArrayList<Session>());
    
    //Singleton
    private static AttackStatusRes _instance;
    private static final int NUM_BUCKETS = 30;
    
    private String idAttack; // maybe useless?
    
    private double totalPercentage;
    private int numCollisions;
    private String etc;
    
    private int numAvailableBuckets;
    private int numWorkingBuckets;
    private int numCompletedBuckets;
    
    BucketRes [] buckets;
       
    public AttackStatusRes(String idAttack) {
        this.idAttack = idAttack;
        buckets = new BucketRes[NUM_BUCKETS];

        // Quick test...
        this.totalPercentage     = 0;
        this.numCollisions       = 0;
        this.etc                 = "tbd";
        this.numAvailableBuckets = 0;
        this.numWorkingBuckets   = 0;
        this.numCompletedBuckets = 0;
        
        // GENERATE BUCKETS!
        for(int i = 0; i < NUM_BUCKETS; i++) {
            buckets[i] = new BucketRes(""+i);
        }
    }
    
    public static AttackStatusRes getAttackStatus(String idAttack) {
        if(_instance == null) {
            _instance = new AttackStatusRes(idAttack);
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
    
    public void setFromJson(String json) {
        JsonElement jsonElement = new JsonParser().parse(json);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        
        this.etc = jsonObject.get("etc").getAsString();
        this.numAvailableBuckets = jsonObject.get("numAvailableBuckets").getAsInt();
        this.numCollisions = jsonObject.get("numCollisions").getAsInt();
        this.numCompletedBuckets = jsonObject.get("numCompletedBuckets").getAsInt();
        this.numWorkingBuckets = jsonObject.get("numWorkingBuckets").getAsInt();
        this.totalPercentage = jsonObject.get("totalPercentage").getAsDouble();
        
        JsonArray jsonBuckets = jsonObject.getAsJsonArray("buckets");
        for(int i=0; i<NUM_BUCKETS; i++) {
            buckets[i].setFromJsonObject(jsonBuckets.get(i).getAsJsonObject());
        }
    }
    
}
