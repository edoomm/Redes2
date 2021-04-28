package com.mycompany.chat.multicast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import javax.swing.ListModel;

public class MonitorReceiver extends Thread {
    
    private MulticastSocket socket;
    private JTextArea chatText;
    private DefaultListModel usersList;
    private String user;
    

    public MonitorReceiver( JTextArea textArea, DefaultListModel users ) throws IOException {
        socket = new MulticastSocket(MessageHandler.PORT);
        socket.joinGroup(InetAddress.getByName(MessageHandler.GROUP_ADDRESS));
        chatText = textArea;
        usersList = users;
    }
    
    
    @Override
    public void run() {
        try {
            while (true) {
                byte[] buffer = new byte[MessageHandler.BUFFER_SIZE];
                DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
                socket.receive(recv);
                byte [] receivedData = recv.getData();
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(receivedData));
                Message message = (Message)ois.readObject();
                int newUserIndex = -1;
                switch ( message.getType() ) {
                    case LEAVE:
                        try {
                            int userIndex = usersList.lastIndexOf(message.getUser());
                            usersList.remove(userIndex);
                            chatText.append(message.getUser() + " se ha ido.\n");
                        } catch (ArrayIndexOutOfBoundsException aobe) {
                            System.out.println("El usuario no está en la lista");
                        }
                    break;
                    case JOIN:
                        newUserIndex = usersList.indexOf(message.getUser());
                        if ( newUserIndex == -1 ) {
                            usersList.addElement(recv.getAddress() + "/" + recv.getPort() + "::" + message.getUser());
                        }
                    break;
                    case SEND:
                        //chatText.append(message.getUser() + ": " + message.getMessageBody() + "\n");
                    break;
                    case NOTIFY:
                        newUserIndex = usersList.indexOf(message.getUser());
                        if ( newUserIndex == -1 ) {
                            usersList.addElement(message.getUser());
                        }
                    break;
                    default:
                        System.out.println("Mensaje desconocido");
                }
                chatText.append(recv.getAddress() + "/" + recv.getPort() + ":: " + message.getUser() + " :: " + message.getType() + " :: " + message.getMessageBody());
            }
        } catch ( ClassNotFoundException cnfe ) {
            cnfe.printStackTrace();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
    }
    
    public void setUserName ( String user ) {
        this.user = user;
    }
}
