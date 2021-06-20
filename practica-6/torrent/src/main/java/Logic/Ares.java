package Logic;

import InformationObjects.FileInformation;
import InformationObjects.RMIInformation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Ares {
    private String rmiHost;
    private String multicastHost;
    private int downloadServerPort;
    private int rmiPort;
    
    private MulticastClient multicastClient;
    private MulticastServer multicastServer;
    private FragmentFileSender fragmentFileSender;
    private ResourceLocatorServerThread resourceLocatorServer;
    private ResourceLocatorClient resourceLocatorClient;
    private String localRootDirectory;

    public Ares(
        String rmiHost, // Puede ser localhost i.e 127.0.0.1
        int downloadServerPort, // debe ser unico
        int rmiPort, /// debe ser unico
        String localRootDirectory)
    {
        this.rmiHost = rmiHost;
        this.downloadServerPort = downloadServerPort;
        this.rmiPort = rmiPort;
        this.localRootDirectory = localRootDirectory;
        
        multicastClient = null;
        multicastServer = null;
        fragmentFileSender = null;
        resourceLocatorServer = null;
        resourceLocatorClient = null;
    }
    
    public void initialize () throws IOException {
        multicastClient = new MulticastClient();
        multicastClient.start();
        
        RMIInformation localRMIServerInfo = new RMIInformation(
            rmiHost, rmiPort);
        multicastServer = new MulticastServer(localRMIServerInfo);
        multicastServer.start();
        
        resourceLocatorServer = new ResourceLocatorServerThread(localRootDirectory, rmiHost, rmiPort);
        resourceLocatorServer.start();
        
        fragmentFileSender = new FragmentFileSender(downloadServerPort);
        fragmentFileSender.start();
        
        resourceLocatorClient = new ResourceLocatorClient();
    }
    
    public void start () {
        while(true) {
            Scanner reader = new Scanner(System.in);
        
            // Read the file name to look for
            System.out.println("Enter the file you wanna look for: ");
            String fileName = reader.nextLine();
            System.out.println("Looking for file in remote servers...");

            // Look for the filename in the know RMI servers
            Map<String, ArrayList<FileInformation>> foundFiles = 
                resourceLocatorClient.lookForFileIn(fileName, multicastClient.getRmiServers());

            System.out.println("Found files");
            
            for (Map.Entry<String, ArrayList<FileInformation>> entry : foundFiles.entrySet()) {
                System.out.println(
                    "["+entry.getKey()+"]"
                    + entry.getValue().get(0).getFile().getName() 
                    + " found on "
                    + entry.getValue().size() + " servers");
            }
            
            System.out.println("Enter the checksum of the wished file: ");
            String checksum = reader.nextLine();
            
            System.out.println("Downoading " + foundFiles.get(checksum).get(0).getFile().getName() + 
                    " from " + foundFiles.get(checksum).size() + "servers...");
            
            FragmentedFileDownloader fileDownloader = 
                    new FragmentedFileDownloader(foundFiles.get(checksum),
                    localRootDirectory);
            fileDownloader.downloadFragments();
            
            System.out.println("File downloaded successfully");
        }
    }
}
