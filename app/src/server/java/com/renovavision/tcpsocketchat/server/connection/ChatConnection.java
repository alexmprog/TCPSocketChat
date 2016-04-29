package com.renovavision.tcpsocketchat.server.connection;

import com.renovavision.tcpsocketchat.model.ChatMessage;
import com.renovavision.tcpsocketchat.model.Message;
import com.renovavision.tcpsocketchat.server.engine.ChatServer;
import com.renovavision.tcpsocketchat.server.engine.Logger;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ChatConnection extends Thread {

    private static final String TAG = ChatConnection.class.getSimpleName();

    private ChatServer chatServer;
    private ChatClient connectClient;
    private Logger logger;
    private BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(2000);

    public ChatConnection(ChatServer chatServer, ChatClient client, Logger logger) {
        this.chatServer = chatServer;
        this.connectClient = client;
        this.logger = logger;
        this.connectClient.chatConnection = this;
    }

    @Override
    public void run() {
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        Socket socket = connectClient.socket;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            String name = dataInputStream.readUTF();

            connectClient.name = name;

            dataOutputStream.writeUTF("Welcome " + name + "\n");
            dataOutputStream.flush();

            chatServer.broadcastMsg(new Message(name + " join our chat.\n"));

            while (true) {
                if (dataInputStream.available() > 0) {
                    String newMsg = dataInputStream.readUTF();

                    chatServer.broadcastMsg(new ChatMessage(name, newMsg));
                }

                while (!messageQueue.isEmpty()) {
                    try {
                        Message message = messageQueue.take();
                        dataOutputStream.writeUTF(message.toString());
                        dataOutputStream.flush();
                    } catch (InterruptedException e) {
                        logger.log(TAG, e);
                    }
                }

            }

        } catch (IOException e) {
            logger.log(TAG, e);
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    logger.log(TAG, e);
                }
            }

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    logger.log(TAG, e);
                }
            }

            chatServer.closeConnection(connectClient);
            chatServer.broadcastMsg(new Message("-- " + connectClient.name + " leaved\n"));
        }

    }

    public void sendMessage(Message message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            logger.log(TAG, e);
        }
    }

}
