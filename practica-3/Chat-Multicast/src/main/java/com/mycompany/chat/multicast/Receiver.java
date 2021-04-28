package com.mycompany.chat.multicast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;

public class Receiver extends Thread {
    private static final int BUFFER_SIZE = 1024;
    private static final String GROUP_ADRESS = "230.0.0.1";
    
    MulticastSocket reader;
    JTextArea jta;
    DefaultListModel ul;
    

    public Receiver( MulticastSocket ms, JTextArea textArea, DefaultListModel users ) throws IOException {
        reader = ms;
        reader = new MulticastSocket(1234);
        reader.joinGroup(InetAddress.getByName(GROUP_ADRESS));
        jta = textArea;
        ul = users;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[BUFFER_SIZE];//crea arreglo de bytes 
                DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
                System.out.println("Esperando mensaje");
                reader.receive(recv);// ya se tiene el datagram packet
                byte [] data = recv.getData(); //aqui no se entienden los datos
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buffer));
                Message message = (Message)ois.readObject();
                switch ( message.getType() ) {
                    case LEAVE:
                        System.out.println("Miembro yendose");
                    break;
                    case JOIN:
                        System.out.println("Miembro conectandose");
                    break;
                    case SEND:
                        System.out.println("Mensaje recibido");
                    break;
                }
                System.out.println(message.getUser() + ": " + message.getMessageBody());
            }
        } catch ( ClassNotFoundException cnfe ) {
            cnfe.printStackTrace();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        Receiver rcvr;
        try {
            rcvr = new Receiver( null, null, null);
            rcvr.start();
        } catch (IOException ex) {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
