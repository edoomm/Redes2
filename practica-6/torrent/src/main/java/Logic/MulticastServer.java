package Logic;

import InformationObjects.RMIInformation;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

/**
 * MulticastServer is a Thread which will be constantly announcing
 * the node's RMI ip address and port.
 */
public class MulticastServer implements Runnable {
    private MulticastSocket multicastSocket;
    private RMIInformation rmiInformation;
    private InetAddress groupAddress;
    private boolean running;
    
    public MulticastServer(RMIInformation rmiInformation) throws IOException {
        groupAddress = InetAddress.getByName(CommonConstants.MULTICAST_ADDRESS);
        multicastSocket = new MulticastSocket(CommonConstants.PORT);
        
        multicastSocket.joinGroup(groupAddress);
        
        this.rmiInformation = rmiInformation;
        running = false;
    }
    
    public void start() {
        Thread announcerThread = new Thread(this);
        running = true;
        announcerThread.start();
    }
    
    public void stop() {
        running = false;
    }
    
    @Override
    public void run() {
        try {
            while(true){
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(rmiInformation);
                oos.flush();
                byte[] messageBytes = bos.toByteArray();
                DatagramPacket messagePacket = new DatagramPacket(
                        messageBytes, 
                        messageBytes.length, 
                        groupAddress, 
                        CommonConstants.PORT);
                multicastSocket.send(messagePacket);
                
                Thread.sleep(500);
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
