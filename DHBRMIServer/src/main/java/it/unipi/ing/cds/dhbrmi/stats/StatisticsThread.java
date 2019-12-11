package it.unipi.ing.cds.dhbrmi.stats;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unipi.ing.cds.dhbrmi.DHBRemoteObj;
import it.unipi.ing.cds.dhbrmi.clientinfo.ClientInfo;
import it.unipi.ing.cds.parameters.Parameters;
import java.util.Map;

public class StatisticsThread extends Thread{
    private DHBRemoteObj server;
    private Map<String, ClientInfo> clients;
    private long lastTimestamp;
    private boolean working;
       
    public StatisticsThread(DHBRemoteObj server, Map<String, ClientInfo> clients) {
    	working = true;
    	this.server = server;
    	this.clients = clients;
    }
    public void stopWorking() {
    	working = false;
    }
    
    private void prompt(String s) {
    	System.out.println("[STATISTICS_THREAD] " + s);
    }
    
    public void run() {
    	prompt("Waking up!");
        while(working) {
            try {
                do {
                    Thread.sleep(Parameters.STATISTICS_THREAD_SLEEP_TIME);
                    
                } while(server.getLastModified() <= lastTimestamp);

                
                prompt("Sending stats... ");
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
                server.notifyTomcat(Parameters.NACT_STATS_AGGREGATED, par);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        prompt("Teminated");
    }
}