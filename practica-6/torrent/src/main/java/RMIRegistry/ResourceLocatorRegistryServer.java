package RMIRegistry;

import InformationObjects.FileInformation;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ResourceLocatorRegistryServer implements IResourceLocator {
    private String rootDirectory;
    private String host;
    private int port;
    private int serverDownloadPort;
    
    public ResourceLocatorRegistryServer(String directory, String host, int port, int serverDownloaderPort) {
        rootDirectory = directory;
        this.serverDownloadPort = serverDownloaderPort;
        this.host = host;
        this.port = port;
    }
    
    public void initializeRMIServer() {
        try {
            LocateRegistry.createRegistry(port);
            //System.out.println("RMI registry ready.");
        } catch (Exception e) {
            System.out.println("Exception starting RMI registry:");
            e.printStackTrace();
         }
	
	try {
            System.setProperty("java.rmi.server.codebase", 
                "file:///C:\\Users\\Donaldo\\Desktop\\Desktop\\repos"
                + "\\Redes2\\practica-6\\torrent\\target\\classes"
                + "\\InformationObjects\\");
            
            System.setProperty("java.security.policy",
                "C:\\Users\\Donaldo\\Desktop\\Desktop"
                + "\\repos\\Redes2\\practica-6\\torrent\\src"
                + "\\main\\java\\RMIRegistry\\policies.policy");
            
	    ResourceLocatorRegistryServer resourceLocator = 
                new ResourceLocatorRegistryServer(
                    rootDirectory,
                    host,
                    port,
                    serverDownloadPort
                );
	    
            IResourceLocator stub = 
                (IResourceLocator) UnicastRemoteObject.exportObject(resourceLocator, port);

	    // Bind the remote object's stub in the registry
	    Registry registry = LocateRegistry.getRegistry(port);
	    registry.bind("IResourceLocator", stub);

	    //System.err.println("RMI Server initialized...");
	} catch (Exception e) {
	    System.err.println("Server exception: " + e.toString());
	    e.printStackTrace();
	}
    }
    
    /**
     * Implemented method from IResourceLocator interface, it looks
     * recursively in the current rootDirectory and return the list of matches
     * @param fileName 
     */
    @Override
    public List<FileInformation> lookForFilesInformation(String fileName) throws RemoteException {
        List<File> matchingFileNames = getMatchingFileNames(fileName);
        List<FileInformation> files = new ArrayList<FileInformation>();
        
        try {
            for (File file : matchingFileNames){
                files.add(
                    new FileInformation(
                            host, 
                            file, 
                            MD5Checksum.getMD5Checksum(file.getAbsolutePath()), 
                            serverDownloadPort));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return files;
        }
    }
    
    /**
     * Gets all the files recursively contained in one root folder
     * @param folder Root directory folder
     * @return 
     */
    private List<File> listFilesInFolder(File folder) {
        List<File> directoryFiles = new ArrayList<File>();
        
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                directoryFiles.addAll(listFilesInFolder(fileEntry));
            } else {
                directoryFiles.add(fileEntry);
            }
        }
        
        return directoryFiles;
    }
    
    /**
     * Filters the files inside the directory and returns the ones that match 
     * with the fileName requested
     * @param fileName 
     */
    private List<File> getMatchingFileNames(String fileName) {
        List<File> directoryFiles = listFilesInFolder(new File(rootDirectory));
        
        List<File> matchedFiles = new ArrayList<File>();
        
        for (File file : directoryFiles) {
            //System.out.println(file.getAbsoluteFile());
            if (file.getName().contains(fileName)) {
                matchedFiles.add(file.getAbsoluteFile());
            }
        }
        
        return matchedFiles;
    }
    
}
