package RMIRegistry;

import InformationObjects.FileInformation;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IResourceLocator extends Remote {
    public List<FileInformation> lookForFilesInformation(String fileName) throws RemoteException;
}
