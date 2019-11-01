/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.,
 */
package unipi.ce.cds.dhbserver;

import com.google.gson.Gson;
import java.util.Date;
import java.util.Random;

public class Bucket {
    private final String id;
    private double percentage;
    private String idWorker;
    
    private Date dateAllocation;
    private Date lastHeartbeat;
    private Date dateCompleted;
    
    private boolean available;
    
    
    public Bucket(String id, boolean randomize) {
        this.id = id;
        
        if(randomize) {
            this.percentage = new Random().nextInt(101);
            this.idWorker = "Worker #" + new Random().nextInt(101);
            this.available = (Math.random() < 0.05);
            
            this.dateAllocation = new Date();
            this.lastHeartbeat  = new Date();
        }
    }
    
    public void setIdWorker(String idWorker) {
        this.idWorker = idWorker;
    } 
    
    public String JSONStringify() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
