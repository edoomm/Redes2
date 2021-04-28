package com.mycompany.chat.multicast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Sender {

    private static final String GROUP_ADRESS = "230.0.0.1";

    private MulticastSocket socket;
    private InetAddress groupAddress;

    public Sender(MulticastSocket socket) {
        try {
            this.socket = socket;
            groupAddress = InetAddress.getByName(GROUP_ADRESS);
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            MulticastSocket socket = new MulticastSocket( 1234 );
            socket.joinGroup(InetAddress.getByName(GROUP_ADRESS));
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            oos.flush();
            byte[] messageBytes = bos.toByteArray();
            DatagramPacket messagePacket = new DatagramPacket(messageBytes, messageBytes.length, groupAddress,1234);
            System.out.println("Enviando mensaje\n" + message.getUser());
            socket.send(messagePacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        try {
            Sender sender = new Sender(null);
            for ( int i = 0 ; i < 5 ; i++ ) {
                sender.sendMessage(new Message("Donaldo", "Hola", MessageType.JOIN));
                Thread.sleep(1000);
            }
        } catch ( InterruptedException ie ) {
            ie.printStackTrace();
        }
        
    }
}
