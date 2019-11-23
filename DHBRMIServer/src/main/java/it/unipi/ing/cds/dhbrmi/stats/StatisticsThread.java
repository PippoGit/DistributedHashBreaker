/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unipi.ing.cds.dhbrmi.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unipi.ing.cds.dhbrmi.DHBRemoteObj;
import it.unipi.ing.cds.dhbrmi.clientinfo.ClientInfo;
import it.unipi.ing.cds.parameters.Parameters;
import java.util.Map;

/**
 *
 * @author filipposcotto
 */
public class StatisticsThread extends Thread{
    private DHBRemoteObj server;
    private Map<String, ClientInfo> clients;
    private long lastTimestamp;
       
    public StatisticsThread(DHBRemoteObj server, Map<String, ClientInfo> clients) {
    	this.server        = server;
    	this.clients       = clients;
    }
    
    public void run() {
        while(true) {
            try {
                do {
                    Thread.sleep(Parameters.STATISTICS_THREAD_SLEEP_TIME);
                    System.out.println("[STATISTICS_THREAD] Waking up!");
                } while(server.getLastModified() <= lastTimestamp);

                
                System.out.println("[STATISTICS_THREAD] Sending stats... ");
                lastTimestamp = System.currentTimeMillis();
                
                JsonObject par = new JsonObject();
                JsonArray buckets = new JsonArray();

                for(ClientInfo ci : clients.values()) {
                    JsonObject bucket = new JsonObject();
                    bucket.addProperty("id", ci.getBucketNr());
                    bucket.addProperty("inspected", ci.getInspected());
                    bucket.addProperty("foundCollisions", ci.getNumCollisions());
                    bucket.addProperty("lastHeartbeat", ci.getLastHeartbeat());
                    buckets.add(bucket);
                }
                par.add("buckets", buckets);
                System.out.println("TEST:   " + par.toString());
                server.notifyTomcat(Parameters.NACT_STATS_AGGREGATED, par);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
