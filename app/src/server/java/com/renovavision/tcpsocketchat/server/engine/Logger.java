package com.renovavision.tcpsocketchat.server.engine;

// class for logging
public class Logger {

    public void log(String tag, Throwable throwable) {
        System.out.println(tag + ": " + throwable.getMessage());
    }
}
