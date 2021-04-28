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

    private MulticastSocket socket;
    private InetAddress groupAddress;

    public Sender(MulticastSocket socket) {
        try {
            this.socket = socket;
            groupAddress = InetAddress.getByName(MessageHandler.GROUP_ADDRESS);
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(message);
            oos.flush();
            byte[] messageBytes = bos.toByteArray();
            DatagramPacket messagePacket = new DatagramPacket(messageBytes, messageBytes.length, groupAddress, MessageHandler.PORT);
            socket.send(messagePacket);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
