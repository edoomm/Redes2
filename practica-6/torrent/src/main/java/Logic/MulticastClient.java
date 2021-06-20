package Logic;


import InformationObjects.RMIInformation;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

public class MulticastClient implements Runnable {
    private volatile Set<RMIInformation> rmiServers;
    private MulticastSocket multicastSocket;
    private boolean running;
    
    public MulticastClient() throws IOException {
        rmiServers = new TreeSet<RMIInformation>();
        multicastSocket = new MulticastSocket(CommonConstants.PORT);
        multicastSocket.joinGroup(InetAddress.getByName(CommonConstants.MULTICAST_ADDRESS));
    }
    
    public void start() {
        Thread listenerThread = new Thread(this);
        running = true;
        listenerThread.start();
    }
    
    public void stop() {
        running = false;
    }
    
    public Set<RMIInformation> getRmiServers() {
        return rmiServers;
    }
    
    @Override
    public void run() {
        try {    
            while(running) {
                byte[] buffer = new byte[CommonConstants.BUFFER_SIZE];
                DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(recv);
                byte [] receivedData = recv.getData();
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivedData));
                RMIInformation rmiInformation = (RMIInformation)ois.readObject();
                
                rmiServers.add(rmiInformation);
                
                System.out.println("Elements in the tree: " + rmiServers.size());
                
                System.out.println("Received RMI info. : " + rmiInformation);
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
    }
}
