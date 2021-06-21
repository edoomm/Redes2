package Logic;

import java.io.IOException;

public class AresExecutor {
    public static void main(String[] args) {
        int serverNumber = 3;
        
        String rmiHost = "127.0.0.1";
        int rmiPort = 1200 + serverNumber;
        int downloadServerPort = 1100 + serverNumber;
        String localRootDirectory = "C:\\Users\\Donaldo\\Desktop\\Server" + serverNumber + "\\";
        
        Ares ares = new Ares(rmiHost, downloadServerPort, rmiPort, localRootDirectory);
        
        try {
            ares.initialize();
            ares.start();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        
    }
}
