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

public class FragmentedFileDownloader {
    private List<FileInformation> fileSources;
    private byte[][] fileFragments;
    private long fileFragmentLength;
    private int numberOfFragments;
    private String rootDirectory;
    
    public FragmentedFileDownloader(List<FileInformation> fileSources, String rootDirectory) {
        this.fileSources = fileSources;
        long fileSize = fileSources.get(0).getFile().length();
        numberOfFragments = fileSources.size();
        fileFragmentLength = fileSize / numberOfFragments;
        fileFragments = new byte[numberOfFragments][(int)fileFragmentLength];
        
        this.rootDirectory = rootDirectory;
    }
    
    public void downloadFragments () {
        try {
            File file = null;
            for (int i = 0 ; i < numberOfFragments ; i++) {
                file = fileSources.get(i).getFile();
                Thread downloader = 
                new FragmentDownoaderThread(
                    i,
                    i*fileFragmentLength,
                    fileFragmentLength,
                    fileSources.get(i));
                downloader.start();
                downloader.join();
            }
            writeFragmentsToFile(file);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
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
    
    class FragmentDownoaderThread extends Thread {
        private Socket socket;
        private ObjectOutputStream outputStream;
        private FileInformation fileInformation;
        private long fragmentSize;
        private int fragment;
        private long initialPosition;
        
        public FragmentDownoaderThread(int fragment, long initialPosition, long fragmentSize, FileInformation file) {
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
                System.out.println("Sending request");
                outputStream.writeObject(fileInfo);
                outputStream.flush();
                System.out.println("Request sent");
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
                System.out.println("Receiving file: " + fileInformation.getFile().getName());
                while (pendingBytes > 0 && (readBytes = dataInputStream.read(buffer))!= -1) {
                    System.out.println("Reading " + readBytes + " bytes");
                    
                    for(int i = 0 ; i < readBytes ; i++) {
                        System.out.println("Check 1");
                        byte check = fileFragments[(int)fragment][(int)totalReadBytes];
                        System.out.println("Check 2");
                        check = buffer[i];
                        System.out.println("Check 3");
                        fileFragments[(int)fragment][(int)totalReadBytes++] = buffer[i];
                    }
                    
                    pendingBytes -= readBytes;
                    System.out.println("Pending bytes: " + pendingBytes + " / " + fragmentSize);
                    
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
