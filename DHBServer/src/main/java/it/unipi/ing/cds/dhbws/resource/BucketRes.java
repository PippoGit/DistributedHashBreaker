/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.,
 */
package it.unipi.ing.cds.dhbws.resource;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BucketRes {
    private final  String id;
    private double percentage;
    private String workerNickname;
    private String UUIDWorker;
    private int    numCollisions;
    private long   inspected;
    
    private Date dateAllocation;
    private Date lastHeartbeat;
    private Date dateCompleted;
    
    private boolean available;
    
    public BucketRes(String id) {
        this.id            = id;
        this.available     = true;
        this.numCollisions = 0;
        this.inspected     = 0;
    }
    
    public BucketRes(String id, boolean randomize) {
        this.id = id;
       
        // Just for tests ...
        if(randomize) {
            this.percentage = new Random().nextInt(101);
            this.workerNickname = "Worker #" + new Random().nextInt(101);
            this.available = (Math.random() < 0.05);
            
            this.dateAllocation = new Date();
            this.lastHeartbeat  = new Date();
        }
    }
    
    public void setWorkerNickname(String workerNickname) {
        this.workerNickname = workerNickname;
    } 

    public String getUUIDWorker() {
        return UUIDWorker;
    }

    public void setUUIDWorker(String UUIDWorker) {
        this.UUIDWorker = UUIDWorker;
    }

    public String JSONStringify() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    // Not really useful... 
    public void setFromJsonObject(JsonObject jsonObject) {       
        this.percentage = jsonObject.get("percentage").getAsDouble();
        this.workerNickname = jsonObject.get("idWorker").getAsString();
        this.available = jsonObject.get("available").getAsBoolean();
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            this.dateAllocation = dateFormat.parse(jsonObject.get("dateAllocation").getAsString());
            this.lastHeartbeat  = dateFormat.parse(jsonObject.get("lastHeartbeat").getAsString());
            this.dateCompleted  = dateFormat.parse(jsonObject.get("dateCompleted").getAsString());
        } catch (ParseException ex) {
            Logger.getLogger(BucketRes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public Date getDateAllocation() {
        return dateAllocation;
    }

    public void setDateAllocation(Date dateAllocation) {
        this.dateAllocation = dateAllocation;
    }

    public Date getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(Date lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getNumCollisions() {
        return numCollisions;
    }
    
    public void addCollisions(int collisions) {
        this.numCollisions += collisions;
    }
    
    public void setInspected(long inspected) {
        this.inspected = inspected;
    }
    
    public long getInspecetd() {
        return inspected;
    }
}
