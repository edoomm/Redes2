
import RMIRegistry.ResourceLocatorRegistryServer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;


public class ResourceLocatorTests {
    public static void main(String[] args) {
        try {
            ResourceLocatorRegistryServer rlrs = 
                    new ResourceLocatorRegistryServer(
                        "C:\\Users\\Donaldo\\Desktop\\ServerDirectory\\",
                        InetAddress.getLocalHost().toString(),
                        1099
                    );

            rlrs.initializeRMIServer();
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }
        
    }
}
