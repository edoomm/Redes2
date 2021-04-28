package com.mycompany.chat.multicast;

import java.io.Serializable;

/**
 * Enum type to know what kind of message is being sent or received
 * JOIN: Joining to the chat
 * LEAVE: Leaving the chat
 * SEND: Just sending a message
 */
enum MessageType {
    JOIN, LEAVE, SEND
}

public class Message implements Serializable {
    private String user;
    private String messageBody;
    private MessageType type;

    public Message(String user, String messageBody, MessageType type) {
        this.user = user;
        this.messageBody = messageBody;
        this.type = type;
    }
   
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
    
}
