package RMIRegistry;

import InformationObjects.FileInformation;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ResourceLocatorRegistryClient {
    public static List<FileInformation> lookForFiles(String fileName, String host, int port) throws NotBoundException, RemoteException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        IResourceLocator resourceLocator = (IResourceLocator) registry.lookup("IResourceLocator");
        return resourceLocator.lookForFilesInformation(fileName);
    }
}
