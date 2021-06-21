package Logic;

import InformationObjects.FileInformation;
import InformationObjects.FragmentDownloadInformation;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FragmentedFileDownloader {
    private List<FileInformation> fileSources;
    private byte[][] fileFragments;
    private long fileFragmentLength;
    private int numberOfFragments;
    private String rootDirectory;
    private ExecutorService threadPool;
    private final int THREAD_NO = 5;
    
    public FragmentedFileDownloader(List<FileInformation> fileSources, String rootDirectory) {
        this.fileSources = fileSources;
        long fileSize = fileSources.get(0).getFile().length();
        numberOfFragments = fileSources.size();
        fileFragmentLength = fileSize / numberOfFragments;
        fileFragments = new byte[numberOfFragments][(int)fileFragmentLength];
        
        threadPool = Executors.newFixedThreadPool(THREAD_NO);
        
        this.rootDirectory = rootDirectory;
    }
    
    public void downloadFragments () {
        File file = null;
        Thread[] executedThreads = new Thread[numberOfFragments];
        for (int i = 0 ; i < numberOfFragments ; i++) {
            file = fileSources.get(i).getFile();
            Thread downloaderThread = 
                new FragmentDownloaderThread(
                    i,
                    i*fileFragmentLength,
                    fileFragmentLength,
                    fileSources.get(i));
            downloaderThread.start();
            executedThreads[i] = downloaderThread;
        }
        try {
            for (int i = 0 ; i < numberOfFragments ; i++) {
                executedThreads[i].join();
            }
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
        writeFragmentsToFile(file);
    }
    
    private void writeFragmentsToFile(File file) {
        try {
            String fileAbsolutePath = rootDirectory + file.getName();
            FileOutputStream fileWriter = new FileOutputStream(fileAbsolutePath);
            for (int i = 0 ; i < numberOfFragments ; i++) {
                fileWriter.write(fileFragments[i]);
            }
            fileWriter.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    class FragmentDownloaderThread extends Thread {
        private Socket socket;
        private ObjectOutputStream outputStream;
        private FileInformation fileInformation;
        private long fragmentSize;
        private int fragment;
        private long initialPosition;
        
        public FragmentDownloaderThread(int fragment, long initialPosition, long fragmentSize, FileInformation file) {
            String host = file.getServerIpAddress();
            int port = file.getServerPort();
            this.fileInformation = file;
            this.fragmentSize = fragmentSize;
            this.fragment = fragment;
            this.initialPosition = initialPosition;
        }

        @Override
        public void run() {
            establishConnection();
            sendRequest(new FragmentDownloadInformation(
                fileInformation.getFile(),
                initialPosition,
                fragmentSize));
            recibirArchivo();
        }
        
        public void establishConnection () {
            try {
                socket = new Socket (fileInformation.getServerIpAddress(), fileInformation.getServerPort());
                outputStream = new java.io.ObjectOutputStream(socket.getOutputStream());
            } catch ( IOException ioe ) {
                ioe.printStackTrace();
            }
        }
        
        public void sendRequest ( FragmentDownloadInformation fileInfo ) {
            try {
                outputStream.writeObject(fileInfo);
                outputStream.flush();
            } catch ( IOException ioe ) {
                ioe.printStackTrace();
            }
        }
        
        public void recibirArchivo () {
            try {
                long readBytes = 0;
                long pendingBytes = fragmentSize;
                long totalReadBytes = 0;

                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                byte[] buffer = new byte[1500];
                while (pendingBytes > 0 && (readBytes = dataInputStream.read(buffer))!= -1) {
                    
                    for(int i = 0 ; i < readBytes ; i++) {
                        byte check = fileFragments[(int)fragment][(int)totalReadBytes];
                        check = buffer[i];
                        fileFragments[(int)fragment][(int)totalReadBytes++] = buffer[i];
                    }
                    
                    pendingBytes -= readBytes;
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
