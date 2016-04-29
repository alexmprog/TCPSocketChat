package com.renovavision.tcpsocketchat.server.engine;

import com.renovavision.tcpsocketchat.utils.IPUtils;

// chat server, which start from terminal
public class JavaCmdChatServer {

    public static void main(String[] args) {

        JavaCmdChatServer server = new JavaCmdChatServer();

        while (true) {
            // emulate server work
        }
    }

    JavaCmdChatServer() {
        ChatServer chatServer = new ChatServer(8080, new Logger(), new Broadcaster() {
            @Override
            public void broadcastMessage(Object message) {
                System.out.println(message.toString());
            }
        });

        System.out.println(IPUtils.getIpAddresses());

        chatServer.start();
    }
}
