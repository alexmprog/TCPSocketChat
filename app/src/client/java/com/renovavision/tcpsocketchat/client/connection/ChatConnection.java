package com.renovavision.tcpsocketchat.client.connection;

import android.util.Log;

import com.renovavision.tcpsocketchat.client.event.ConnectionClosedEvent;
import com.renovavision.tcpsocketchat.client.event.ConnectionOpenedEvent;
import com.renovavision.tcpsocketchat.client.event.MessageReceivedEvent;
import com.renovavision.tcpsocketchat.model.Message;
import com.renovavision.tcpsocketchat.utils.BusManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ChatConnection extends Thread {

    private static final String TAG = ChatConnection.class.getSimpleName();

    private String name;
    private String dstAddress;
    private int dstPort;

    private BlockingQueue<Message> messageQueue = new ArrayBlockingQueue<>(2000);

    private boolean goOut = false;

    public ChatConnection(String name, String address, int port) {
        this.name = name;
        this.dstAddress = address;
        this.dstPort = port;
    }

    @Override
    public void run() {
        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;

        try {
            socket = new Socket(dstAddress, dstPort);
            dataOutputStream = new DataOutputStream(
                    socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream.writeUTF(name);
            dataOutputStream.flush();

            BusManager.getInstance().postInMainThread(ConnectionOpenedEvent.INSTANCE);

            while (!goOut) {
                if (dataInputStream.available() > 0) {
                    BusManager.getInstance().postInMainThread(new MessageReceivedEvent(dataInputStream.readUTF()));
                }

                while (!messageQueue.isEmpty()) {
                    try {
                        Message message = messageQueue.take();
                        dataOutputStream.writeUTF(message.toString());
                        dataOutputStream.flush();
                    } catch (InterruptedException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            BusManager.getInstance().postInMainThread(ConnectionClosedEvent.INSTANCE);
        }

    }

    public void sendMessage(Message message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void disconnect() {
        goOut = true;
    }
}
