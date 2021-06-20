
import InformationObjects.FileInformation;
import RMIRegistry.ResourceLocatorRegistryClient;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class ResourceLocatorClientTests {
    public static void main(String[] args) {
        try {
            List<FileInformation> foundFiles = 
                    ResourceLocatorRegistryClient.lookForFiles("file.txt", "127.0.0.1", 1202);
            
            for (FileInformation fileInformation : foundFiles) {
                System.out.println(fileInformation);
            }
        } catch (RemoteException re) {
            re.printStackTrace();
        } catch (NotBoundException nbe) {
            nbe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
