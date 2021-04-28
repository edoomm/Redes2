package com.mycompany.chat.multicast;

import java.io.IOException;
import java.net.MulticastSocket;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;

public class MessageHandler {
    public static final String MC_ADDRESS = "230.0.0.1";
    public static final int PORT = 9013;
    public static final int BUFFER_SIZE = 1024; 
    
    private Receiver receiver;
    private Sender sender;
    
    public MessageHandler ( JTextArea textArea, DefaultListModel userList ) throws IOException{
        MulticastSocket socket = new MulticastSocket(PORT);
        receiver = new Receiver( socket, textArea, userList );
        receiver.start();
        sender = new Sender( socket );
    }
    
    
    
}
