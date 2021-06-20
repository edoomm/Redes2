
import InformationObjects.FileInformation;
import RMIRegistry.ResourceLocatorRegistryClient;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class ResourceLocatorClientTests {
    public static void main(String[] args) {
        try {
            ResourceLocatorRegistryClient rlrc = 
                new ResourceLocatorRegistryClient("127.0.0.1", 1099);
            
            List<FileInformation> foundFiles = rlrc.lookForFiles("file.txt");
            
            for (FileInformation fileInformation : foundFiles) {
                System.out.println(fileInformation);
            }
        } catch (RemoteException re) {
            re.printStackTrace();
        } catch (NotBoundException nbe) {
            nbe.printStackTrace();
        }
    }
}
