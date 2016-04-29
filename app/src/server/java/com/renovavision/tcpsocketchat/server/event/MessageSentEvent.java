package com.renovavision.tcpsocketchat.server.event;

import android.support.annotation.NonNull;

import com.renovavision.tcpsocketchat.model.Message;

public class MessageSentEvent {

    @NonNull
    public Message message;

    public MessageSentEvent(@NonNull Message message) {
        this.message = message;
    }
}
