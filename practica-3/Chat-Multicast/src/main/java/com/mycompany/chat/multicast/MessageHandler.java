package com.mycompany.chat.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;

public class MessageHandler {
    public static final String GROUP_ADDRESS = "230.0.0.1";
    public static final int PORT = 1234;
    public static final int BUFFER_SIZE = 1024;
    
    private Receiver receiver;
    private Sender sender;
    
    public MessageHandler ( JTextArea textArea, DefaultListModel userList ) throws IOException {
        MulticastSocket socket = new MulticastSocket(PORT);
        socket.joinGroup(InetAddress.getByName(GROUP_ADDRESS));
        receiver = new Receiver(socket, textArea, userList);
        receiver.start();
        sender = new Sender( socket );
    }
    
    public void send( String user, String messageBody, MessageType type ) {
        sender.sendMessage(new Message(user, messageBody, type));
    }
    
    public void setUserName(String user) {
        receiver.setUserName( user );
    }
    
}
