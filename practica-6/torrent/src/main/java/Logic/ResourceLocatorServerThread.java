package Logic;

import RMIRegistry.ResourceLocatorRegistryServer;
import java.net.InetAddress;

public class ResourceLocatorServerThread extends Thread {
    private ResourceLocatorRegistryServer rlrs;
    
    public ResourceLocatorServerThread(String localRootdirectory, String host, int port, int downloadServerPort) {
        rlrs = new ResourceLocatorRegistryServer(localRootdirectory, host, port, downloadServerPort);
    }
    
    @Override
    public void run() {
        rlrs.initializeRMIServer();
    }
}
