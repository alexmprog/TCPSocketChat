package com.renovavision.tcpsocketchat.model;

import android.support.annotation.NonNull;

public class Message {

    @NonNull
    public String message;

    public Message(@NonNull String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
