package Logic;

import InformationObjects.FileInformation;
import InformationObjects.RMIInformation;
import RMIRegistry.ResourceLocatorRegistryClient;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ResourceLocatorClient {
    private Set<FileInformation> foundFilesAcrossRMIServers;
    private Lock filesSetLock;
    private String fileName;
    
    public ResourceLocatorClient() {
        filesSetLock = new ReentrantLock();
    }
    
    public void addFoundFiles(List<FileInformation> foundFiles) {
        filesSetLock.lock();
        try {
            foundFilesAcrossRMIServers.addAll(foundFiles);
        } finally {
            filesSetLock.unlock();
        }
    }
    
    public Map<String, ArrayList<FileInformation>> lookForFileIn(String fileName, List<RMIInformation> resourceServers) {
        try {
            foundFilesAcrossRMIServers = new TreeSet<FileInformation>();
            this.fileName = fileName;
            for (RMIInformation rmiServer : resourceServers){
                Thread resourceLocatorThread = new ResourceLocatorClientThread(rmiServer);
                resourceLocatorThread.start();
                resourceLocatorThread.join();
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return groupFilesByChecksum();
    }
    
    public Map<String, ArrayList<FileInformation>> groupFilesByChecksum () {
        Map<String, ArrayList<FileInformation>> groupedFiles = new TreeMap<String, ArrayList<FileInformation>>();
        
        for (FileInformation fileInfo : foundFilesAcrossRMIServers) {
            if(groupedFiles.containsKey(fileInfo.getChecksum())) {
                var list = groupedFiles.get(fileInfo.getChecksum());
                list.add(fileInfo);
                groupedFiles.put(fileInfo.getChecksum(), list);
            } else {
                groupedFiles.put(fileInfo.getChecksum(), new ArrayList<FileInformation>(){{add(fileInfo);}});
            }
        }
        
        return groupedFiles;
    }
    
    class ResourceLocatorClientThread extends Thread {
        private RMIInformation rmiServerInformation;
        
        public ResourceLocatorClientThread(RMIInformation rmiServer) {
            rmiServerInformation = rmiServer;
        }
        
        @Override
        public void run() {
            try {
                List<FileInformation> filesInformation = 
                    ResourceLocatorRegistryClient.lookForFiles(
                        fileName, 
                        rmiServerInformation.getRmiIpAddress(),
                        rmiServerInformation.getRmiPort());
                addFoundFiles(filesInformation);
            } catch (NotBoundException nbe) {
                nbe.printStackTrace();
            } catch (RemoteException re) {
                re.printStackTrace();
                        
            }
        }
    }
}
