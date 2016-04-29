package com.renovavision.tcpsocketchat.client.event;

public class MessageReceivedEvent {

    public String message;

    public MessageReceivedEvent(String message) {
        this.message = message;
    }
}
