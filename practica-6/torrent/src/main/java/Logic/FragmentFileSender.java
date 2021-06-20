package Logic;

import InformationObjects.FragmentDownloadInformation;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class FragmentFileSender extends Thread {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private boolean active;
    private int port;
    
    public FragmentFileSender (int port) {
        serverSocket = null;
        clientSocket = null;
        active = true;
        this.port = port;
    }
    
    @Override
    public void run() {
        try {
            initializeServer();
            while(active) {
                System.out.println("Waiting for connections on FragmentFileSender...");
                clientSocket = serverSocket.accept();
                new FileSenderThread(clientSocket).start();
                System.out.println("New connection");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public void initializeServer () {
        try {
            serverSocket = new ServerSocket(port);
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
    class FileSenderThread extends Thread {
        private Socket client;
        private ObjectInputStream inputStream;
        
        public FileSenderThread(Socket client) {
            try {
                this.client = client;
                inputStream = new ObjectInputStream(client.getInputStream());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
        @Override
        public void run() {
            sendFile();
        }
        
        public void sendFile () {
            try {
                FragmentDownloadInformation fileInfo = (FragmentDownloadInformation)inputStream.readObject();
                System.out.println("Received request for " + fileInfo);
                String fileName = fileInfo.getFile().getAbsolutePath();

                // Flujo para leer la información del archivo
                RandomAccessFile raf = new RandomAccessFile(fileInfo.getFile(), "r");
                raf.seek(fileInfo.getInitialPosition());
                
                DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
                byte[] buffer = new byte[1500];
                long pendingBytes = fileInfo.getFragmentSize();
                int bytesLeidos = 0;
                
                
                // Mientras falten bytes por enviar y se puedan leer bytes del 
                // archivo, se envía un paquete de información
                System.out.println("SendingFile: " + fileInfo.getFile().getName());

                while (pendingBytes > 0 && (bytesLeidos = raf.read(buffer, 0, (int)fileInfo.getFragmentSize())) != -1) {
                    outputStream.write(buffer, 0, bytesLeidos);
                    outputStream.flush();
                    pendingBytes -= bytesLeidos;
                }

                outputStream.close();
                raf.close();
                System.out.println("File: " + fileInfo.getFile().getName() + " sent");
            } catch(IOException ioe) {
                ioe.printStackTrace();
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            } 
        }
    }
}
