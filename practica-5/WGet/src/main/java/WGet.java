
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;



public class WGet {
    private Queue<String> resourcesToDownload;
    private Queue<String> resourcesToExplore;
    private Set<String> exploredResources;
    
    public WGet () {
        exploredResources = new HashSet<String>();
        
    }
    
    class HTMLResourceDownloaderThread extends Thread {
        
    }
    
    class ResourceDownloaderThread extends Thread {
        
    }
}
