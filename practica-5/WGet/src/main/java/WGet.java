
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;



public class WGet {
    
    private static final int POOL_SIZE = 4;
    
    private URL url;
    private Queue<String> resourcesToDownload;
    private Queue<String> resourcesToExplore;
    private Set<String> exploredResources;
    private Set<String> downloadedResources;
    private volatile int runningThreads;
    private Lock runningThreadsLock;
    private Lock exploredResourcesLock;
    private Lock downloadedResourcesLock;
    private Lock resourcesToExploreLock;
    private Lock resourcesToDownloadLock;
    private ExecutorService threadPool;
    private String rootURL;
    private String protocol;
    
    public WGet (String initialURL) throws MalformedURLException{
        exploredResources = new HashSet<String>();
        downloadedResources = new HashSet<String>();
        resourcesToDownload = new LinkedBlockingQueue<String>();
        resourcesToExplore = new LinkedBlockingQueue<String>();
        runningThreads = 0;
        runningThreadsLock = new ReentrantLock();
        exploredResourcesLock = new ReentrantLock();
        downloadedResourcesLock = new ReentrantLock();
        resourcesToExploreLock = new ReentrantLock();
        resourcesToDownloadLock = new ReentrantLock();
        rootURL = (new URL(initialURL)).getHost();
        protocol = (new URL(initialURL)).getProtocol();
        resourcesToDownload.add(rootURL + (new URL(initialURL)).getPath());
        FileExplorerUtilities.createFilePath(new File(rootURL));
        threadPool = Executors.newFixedThreadPool(POOL_SIZE);
    }
    
    /**
     * Increases the number of runningThreads. (Thread safe)
     */
    private void increaseRunningThreads() {
        // We lock the acces to the runninghreads member
        runningThreadsLock.lock();
        try {
            runningThreads++;
        } finally {
            runningThreadsLock.unlock();
        }
    }
    
    /**
     * Decreases the number of runningThreads. (Thread safe)
     */
    private void decreaseRunningThreads() {
        // We lock the acces to the runningThreads member
        runningThreadsLock.lock();
        try {
            runningThreads--;
        } finally {
            runningThreadsLock.unlock();
        }
    }
    
    /**
     * Checks whether a resource has already been explored.
     * @param resourceName Name of the resource
     * @return boolean
     */
    private boolean isResourceExplored(String resourceName) {
        exploredResourcesLock.lock();
        boolean isExplored = false;
        try {
            isExplored = exploredResources.contains(resourceName);
        } finally {
            exploredResourcesLock.unlock();
            return isExplored;
        }
    }
    
    /**
     * Adds a resource name to the exploredResources set.
     * @param resourceName Name of the resource
     */
    private void addExploredResource(String resourceName) {
        exploredResourcesLock.lock();
        try {
            exploredResources.add(resourceName);
        } finally {
            exploredResourcesLock.unlock();
        }
    }
    
    /**
     * Checks whether a resource has already been downloaded.
     * @param resourceName Name of the resource
     * @return boolean
     */
    private boolean isResourceDownloaded(String resourceName) {
        downloadedResourcesLock.lock();
        boolean isExplored = false;
        try {
            isExplored = downloadedResources.contains(resourceName);
        } finally {
            downloadedResourcesLock.unlock();
            return isExplored;
        }
    }
    
    /**
     * Adds a resource name to the downloadedResources set.
     * @param resourceName Name of the resource
     */
    private void addDownloadedResource(String resourceName) {
        downloadedResourcesLock.lock();
        try {
            downloadedResources.add(resourceName);
        } finally {
            downloadedResourcesLock.unlock();
        }
    }
    
    /**
     * Adds a resource to the resourcesToDownload queue.
     * @param resourceName Name of the resource
     */
    private void addResourceToDownload(String resourceName) {
        resourcesToDownloadLock.lock();
        try {
            resourcesToDownload.add(resourceName);
        } finally {
            resourcesToDownloadLock.unlock();
        }
    }
    
    /**
     * Removes the element at the fron of the resourcesToDownload queue and 
     * returns it.
     * @param resourceName Name of the resource
     * @return The name of the resource at the head of the queue.
     */
    private String retrieveResourceToDownload() {
        resourcesToDownloadLock.lock();
        String headResource = "";
        try {
            headResource = resourcesToDownload.poll();
        } finally {
            resourcesToDownloadLock.unlock();
            return headResource;
        }
    }
    
    /**
     * Adds a resource to the resourcesToExplore queue.
     * @param resourceName Name of the resource
     */
    private void addResourceToExplore(String resourceName) {
        resourcesToExploreLock.lock();
        try {
            resourcesToExplore.add(resourceName);
        } finally {
            resourcesToExploreLock.unlock();
        }
    }
    
    /**
     * Removes the element at the fron of the resourcesToDownload queue and 
     * returns it.
     * @param resourceName Name of the resource
     * @return The name of the resource at the head of the queue.
     */
    private String retrieveResourceToExplore() {
        resourcesToExploreLock.lock();
        String headResource = "";
        try {
            headResource = resourcesToExplore.poll();   
        } finally {
            resourcesToExploreLock.unlock();
            return headResource;
        }
    }
    
    public void start() throws IOException{
        do {
            resourcesToDownloadLock.lock();
            try {
                if ( !resourcesToDownload.isEmpty() ) {
                    String next = retrieveResourceToDownload();
                    System.out.println("Getting from to download resource " + next);
                    if (!isResourceDownloaded(next)) {
                        threadPool.execute(new ResourceDownloaderThread(next));
                        increaseRunningThreads();
                    }
                }
            } catch (IOException ioe) {
                System.out.println("Error whlie getting a resource");
            }finally {
                resourcesToDownloadLock.unlock();
            }
            resourcesToExploreLock.lock();
            try {
                if ( !resourcesToExplore.isEmpty() ) {
                    threadPool.execute(new HTMLResourceExplorerThread(retrieveResourceToExplore()));
                    increaseRunningThreads();
                }
            } finally {
                resourcesToExploreLock.unlock();
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (!resourcesToDownload.isEmpty() || !resourcesToExplore.isEmpty() || runningThreads > 0);
        threadPool.shutdownNow();
    }
    
    class ResourceDownloaderThread extends Thread {
        private InputStream contentStream;
        private FileOutputStream fileOutputStream;
        private String resourceName;
        private boolean containsReferences;
        
        public ResourceDownloaderThread(String resourceName) throws IOException {
            // TODO: Verify that the file path exists, otherwise create it
            System.out.println(protocol + "://" + resourceName);
            URL url = new URL(protocol + "://" + resourceName);
            String fileName = "";
            if ( url.getFile().lastIndexOf("/") == url.getFile().length() - 1 )
                fileName = "index.html";
            
            this.resourceName = rootURL + url.getPath() + fileName;
            System.out.println(this.resourceName);
            FileExplorerUtilities.createFilePath(new File(this.resourceName));
            this.fileOutputStream = new FileOutputStream(this.resourceName);
            
            URLConnection urlConnection = url.openConnection();
            this.contentStream = urlConnection.getInputStream();
            // If the MIME type of the file is any of the specified, 
            // it will turn containsReference to true
            containsReferences = false;
            if ( urlConnection.getContentType() != null ) {
                containsReferences = containsReferences || urlConnection.getContentType().contains("html");
                containsReferences = containsReferences || urlConnection.getContentType().contains("php");
            }
        }
        
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int readBytes;
            try {
                while ((readBytes = contentStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, readBytes);
                }
                System.out.println("Adding resource to explore: " + resourceName);
                addDownloadedResource(resourceName);
                // If file can have references to other resources, we
                // enqueue it to the resourcesToExplore queue
                if (containsReferences) {
                        addResourceToExplore(resourceName);
                }
                contentStream.close();
                fileOutputStream.close();
                decreaseRunningThreads();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    class HTMLResourceExplorerThread extends Thread {
        private String fileName;
        
        public HTMLResourceExplorerThread(String fileName) {
            this.fileName = fileName;
        }
        
        @Override
        public void run() {
            try {
                System.out.println("Exploring: " + fileName);
                List<String> fileResources = 
                    HTMLHandler.getTagsContent(fileName);
                for (String resource : fileResources) {
                    if (!isResourceDownloaded(resource)) {
                        if ( !resource.contains("?") )
                            addResourceToDownload(resource);
                    }
                }
                addExploredResource(fileName);
                System.out.println("Finished exploring: " + fileName);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
