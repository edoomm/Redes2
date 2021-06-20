package RMIRegistry;

import InformationObjects.FileInformation;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class ResourceLocatorRegistryClient {
    private Registry registry;
    
    public ResourceLocatorRegistryClient (String host, int port) throws RemoteException {
        registry = LocateRegistry.getRegistry(host, port);
    }
    
    public List<FileInformation> lookForFiles(String fileName) throws NotBoundException, RemoteException {
        IResourceLocator resourceLocator = (IResourceLocator) registry.lookup("IResourceLocator");
        return resourceLocator.lookForFilesInformation(fileName);
    }
}
