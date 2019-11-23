package it.unipi.ing.cds.dhbrmi.clientinfo;

import java.util.ArrayList;
import java.util.UUID;

import it.unipi.ing.cds.parameters.Parameters;

public class ClientInfo {
    private UUID id;
    private String nickName;
    private String hostIP;
    private int hostPort;

    // Statistics
    private int bucketNr;
    private ArrayList<byte[]> collisionsFound;
    private long inspected;
    private String url;
    private long lastHeartBeat;

    public ClientInfo(String nickName, String hostIP, int hostPort) {
        inspected = 0;
        collisionsFound = new ArrayList<byte[]>();
        this.nickName = nickName;
        this.hostIP = hostIP;
        this.hostPort = hostPort;
        this.url = "//" + hostIP + ":" + Integer.toString(hostPort) + "/" + nickName;
        this.id = UUID.randomUUID();
        while(this.id.compareTo(Parameters.noAttackPlanned) == 0 || this.id.compareTo(Parameters.noAvailableBucket) == 0 ||
	    		this.id.compareTo(Parameters.remoteError) == 0 || this.id.compareTo(Parameters.connectionError) == 0 ) {
        	this.id = UUID.randomUUID();
        }
        lastHeartBeat = System.currentTimeMillis();
    }
    public void beats() {
        lastHeartBeat = System.currentTimeMillis();
    }
    public boolean isActive() {
        return lastHeartBeat > System.currentTimeMillis() - Parameters.GUARD_TIME;
    }
    public String getUrl() {
        return url;
    }
    public String getId() {
        return id.toString();
    }
    public String getNickName() {
        return nickName;
    }
    public void setBucketNr(int bucketNr) {
        this.bucketNr = bucketNr;
    }
    public int getBucketNr() {
        return bucketNr;
    }
    public long getInspected() {
        return inspected;
    }
    public void updateCollisions(ArrayList<byte[]> partialCollisions) {
        collisionsFound.addAll(partialCollisions);
    }
    public void updateInspected(long inspected) {
        this.inspected += inspected;
    }
    public String getHostIP() {
        return hostIP;
    }
    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }
    public int getHostPort() {
        return hostPort;
    }
    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }
}