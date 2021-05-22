
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;



public class WGet {
    private URL url;
    private Queue<String> resourcesToDownload;
    private Queue<String> resourcesToExplore;
    private Set<String> exploredResources;
    private volatile int runningThreads;
    private Lock runningThreadsLock;
    
    public WGet (String initialURL) {
        exploredResources = new HashSet<String>();
        resourcesToDownload = new LinkedBlockingQueue<String>();
        resourcesToExplore = new LinkedBlockingQueue<String>();
        runningThreads = 0;
        runningThreadsLock = new ReentrantLock();
        resourcesToDownload.add(initialURL);
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
    
    
    
    public void start() {
        do {
            
        } while (!resourcesToDownload.isEmpty() || !resourcesToExplore.isEmpty() || runningThreads > 0);
    }
    
    class HTMLResourceDownloaderThread implements Runnable {
        private InputStream contentStream;
        private FileOutputStream fileOutputStream;
        
        public HTMLResourceDownloaderThread(
                InputStream contentStream, 
                String fileName) 
                throws IOException
        {
            fileOutputStream = new FileOutputStream(fileName);
            contentStream = contentStream;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int readBytes;
            try {
                while ((readBytes = contentStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer);
                }
                contentStream.close();
                fileOutputStream.close();
                decreaseRunningThreads();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
    
    class ResourceDownloaderThread extends Thread {
        
    }
}
