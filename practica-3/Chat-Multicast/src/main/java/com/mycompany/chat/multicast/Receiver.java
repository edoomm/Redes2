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

public class Receiver extends Thread {
    
    private MulticastSocket socket;
    private JTextArea chatText;
    private DefaultListModel usersList;
    private String user;
    

    public Receiver( MulticastSocket ms, JTextArea textArea, DefaultListModel users ) throws IOException {
        socket = ms;
        chatText = textArea;
        usersList = users;
    }
    
    public void sendMessage(Message message) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            oos.flush();
            byte[] messageBytes = bos.toByteArray();
            DatagramPacket messagePacket = new DatagramPacket(messageBytes, messageBytes.length, InetAddress.getByName(MessageHandler.GROUP_ADDRESS), MessageHandler.PORT);
            socket.send(messagePacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
                int newUserIndex = 0;
                switch ( message.getType() ) {
                    case LEAVE:
                        int userIndex = usersList.lastIndexOf(message.getUser());
                        usersList.remove(userIndex);
                        chatText.append(message.getUser() + " se ha ido.\n");
                    break;
                    case JOIN:
                        newUserIndex = usersList.indexOf(message.getUser());
                        if ( newUserIndex == -1 ) {
                            usersList.addElement(message.getUser());
                            chatText.append(message.getUser() + " se ha unido.\n");
                        }
                        if ( !user.equals(message.getUser()) )
                            sendMessage(new Message(user, "", MessageType.NOTIFY));
                    break;
                    case SEND:
                        chatText.append(message.getUser() + ": " + message.getMessageBody() + "\n");
                    break;
                    case NOTIFY:
                        newUserIndex = usersList.indexOf(message.getUser());
                        if ( newUserIndex == -1 ) {
                            System.out.println("Agregando a " + message.getUser());
                            usersList.addElement(message.getUser());
                        } else {
                            System.out.println("Usuario " + message.getUser() + " ya agregado");
                        }
                    break;
                    default:
                        System.out.println("Mensaje desconocido");
                }
                //System.out.println(message.getType() + "-" + message.getUser() + ": " + message.getMessageBody());
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
