package it.unipi.ing.cds.util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.NoSuchElementException;

public class AvailablePortFinder {

    public static final int MIN_PORT_NUMBER = 1024;
    public static final int MAX_PORT_NUMBER = 49151;
    
    private AvailablePortFinder() {}

    public static int getNextAvailable() {
        return getNextAvailable(MIN_PORT_NUMBER);
    }

    public static int getNextAvailable(int fromPort) {
        if (fromPort < MIN_PORT_NUMBER || fromPort > MAX_PORT_NUMBER)
            throw new IllegalArgumentException("Invalid start port: " + fromPort);

        for (int i = fromPort; i <= MAX_PORT_NUMBER; i++) {
            if (available(i))
                return i;
        }

        throw new NoSuchElementException("Could not find an available port " + "above " + fromPort);
    }

    public static boolean available(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER)
            throw new IllegalArgumentException("Invalid start port: " + port);

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) 
                ds.close();

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                	System.out.println("An error has occurred in finding an available port");
                    /* should not be thrown */
                }
            }
        }
        return false;
    }
}