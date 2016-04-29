package com.renovavision.tcpsocketchat.model;

import android.support.annotation.NonNull;

public class ChatMessage extends Message {

    @NonNull
    public String author;

    public ChatMessage(@NonNull String author, @NonNull String message) {
        super(message);
        this.author = author;
        this.message = message;
    }

    @Override
    public String toString() {
        return author + ": " + message;
    }
}
