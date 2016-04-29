package com.renovavision.tcpsocketchat.client.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.renovavision.tcpsocketchat.BuildConfig;
import com.renovavision.tcpsocketchat.R;
import com.renovavision.tcpsocketchat.client.connection.ChatConnection;
import com.renovavision.tcpsocketchat.client.event.ConnectionClosedEvent;
import com.renovavision.tcpsocketchat.client.event.ConnectionOpenedEvent;
import com.renovavision.tcpsocketchat.client.event.MessageReceivedEvent;
import com.renovavision.tcpsocketchat.model.ChatMessage;
import com.renovavision.tcpsocketchat.utils.BusManager;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClientActivity extends AppCompatActivity {

    @Bind(R.id.login_container)
    LinearLayout loginContainer;

    @Bind(R.id.chat_container)
    LinearLayout chatContainer;

    @Bind(R.id.username)
    EditText editTextUserName;

    @Bind(R.id.address)
    EditText editTextAddress;

    @Bind(R.id.say)
    EditText editTextSay;

    @Bind(R.id.chatmsg)
    TextView chatMsg;

    @Bind(R.id.port)
    TextView textPort;

    private StringBuilder messageLog = new StringBuilder();

    ChatConnection chatConnection = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        ButterKnife.bind(this);

        BusManager.getInstance().registerMainThreadListener(this);

        textPort.setText("Port: " + BuildConfig.SERVER_PORT);
    }

    @OnClick(R.id.disconnect)
    public void onDisconnect(View view) {
        if (chatConnection == null) {
            return;
        }
        chatConnection.disconnect();
    }

    @OnClick(R.id.send)
    public void onMessageSend(View view) {
        if (editTextSay.getText().toString().equals("")) {
            return;
        }

        if (chatConnection == null) {
            return;
        }

        chatConnection.sendMessage(new ChatMessage(editTextUserName.getText().toString()
                , editTextSay.getText().toString() + "\n"));
    }

    @OnClick(R.id.connect)
    public void onConnect(View view) {
        String textUserName = editTextUserName.getText().toString();
        if (textUserName.equals("")) {
            Toast.makeText(this, "Enter User Name",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String textAddress = editTextAddress.getText().toString();
        if (textAddress.equals("")) {
            Toast.makeText(this, "Enter Address",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        messageLog = new StringBuilder();
        chatMsg.setText(messageLog);

        chatConnection = new ChatConnection(
                textUserName, textAddress, BuildConfig.SERVER_PORT);
        chatConnection.start();
    }

    @Subscribe
    public void onConnectionOpened(ConnectionOpenedEvent event) {
        loginContainer.setVisibility(View.GONE);
        chatContainer.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onConnectionClosed(ConnectionClosedEvent event) {
        loginContainer.setVisibility(View.VISIBLE);
        chatContainer.setVisibility(View.GONE);
    }

    @Subscribe
    public void onMessageReceived(MessageReceivedEvent event) {
        messageLog.append(event.message);
        chatMsg.setText(messageLog.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatConnection != null) {
            chatConnection.disconnect();
        }
        ButterKnife.unbind(this);
        BusManager.getInstance().unregisterMainThreadListener(this);
    }
}
