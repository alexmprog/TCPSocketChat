package com.renovavision.tcpsocketchat.server.event;

public class ServerStartedEvent {

    public int serverPort;

    public ServerStartedEvent(int serverPort) {
        this.serverPort = serverPort;
    }

}
