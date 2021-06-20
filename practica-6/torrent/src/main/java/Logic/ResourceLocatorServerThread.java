package Logic;

import RMIRegistry.ResourceLocatorRegistryServer;
import java.net.InetAddress;

public class ResourceLocatorServerThread extends Thread {
    private ResourceLocatorRegistryServer rlrs;
    
    public ResourceLocatorServerThread(String localRootdirectory, String host, int port) {
        rlrs = new ResourceLocatorRegistryServer(localRootdirectory, host, port);
    }
    
    @Override
    public void run() {
        rlrs.initializeRMIServer();
    }
}
