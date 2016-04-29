package com.renovavision.tcpsocketchat.server.engine;

import com.renovavision.tcpsocketchat.model.Message;
import com.renovavision.tcpsocketchat.server.connection.ChatClient;
import com.renovavision.tcpsocketchat.server.connection.ChatConnection;
import com.renovavision.tcpsocketchat.server.event.MessageSentEvent;
import com.renovavision.tcpsocketchat.server.event.ServerStartedEvent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// chat server
public class ChatServer extends Thread {

    private static final String TAG = ChatServer.class.getSimpleName();

    public StringBuilder logBuilder = new StringBuilder();

    private List<ChatClient> clientList = new ArrayList<>();

    private ServerSocket serverSocket;

    private Logger logger;

    private Broadcaster broadcaster;

    private int serverPort;

    public ChatServer(int serverPort, Logger logger, Broadcaster broadcaster) {
        this.serverPort = serverPort;
        this.logger = logger;
        this.broadcaster = broadcaster;
    }

    @Override
    public void run() {

        Socket socket = null;

        try {
            serverSocket = new ServerSocket(serverPort);
            broadcaster.broadcastMessage(new ServerStartedEvent(serverSocket.getLocalPort()));

            while (true) {

                socket = serverSocket.accept();
                ChatClient client = new ChatClient();
                client.socket = socket;

                openConnection(client);
            }

        } catch (IOException e) {
            logger.log(TAG, e);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.log(TAG, e);
                }
            }
        }

    }

    public void closeConnection(ChatClient client) {
        if (client == null) {
            return;
        }

        clientList.remove(client);
    }

    public void openConnection(ChatClient client) {
        if (client == null) {
            return;
        }

        clientList.add(client);
        ChatConnection chatConnection = new ChatConnection(this, client, logger);
        chatConnection.start();
    }

    public void shutdown() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.log(TAG, e);
            }
        }
    }

    public synchronized void broadcastMsg(Message message) {
        if (clientList.isEmpty()) {
            return;
        }

        for (int i = 0; i < clientList.size(); i++) {
            ChatClient client = clientList.get(i);
            ChatConnection connection = client.chatConnection;
            if (connection == null) {
                continue;
            }
            connection.sendMessage(message);
            logBuilder.append("- send to ").append(client.name).append("\n");
        }

        broadcaster.broadcastMessage(new MessageSentEvent(new Message(logBuilder.toString())));
    }

}