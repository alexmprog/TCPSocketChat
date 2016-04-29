package com.renovavision.tcpsocketchat.server.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.renovavision.tcpsocketchat.BuildConfig;
import com.renovavision.tcpsocketchat.server.engine.Broadcaster;
import com.renovavision.tcpsocketchat.server.engine.ChatServer;
import com.renovavision.tcpsocketchat.server.engine.Logger;
import com.renovavision.tcpsocketchat.utils.BusManager;
import com.renovavision.tcpsocketchat.utils.IPUtils;
import com.renovavision.tcpsocketchat.R;
import com.renovavision.tcpsocketchat.server.event.MessageSentEvent;
import com.renovavision.tcpsocketchat.server.event.ServerStartedEvent;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ServerActivity extends AppCompatActivity {

    @Bind(R.id.info_ip_view)
    TextView infoIpView;

    @Bind(R.id.info_port_view)
    TextView infoPortView;

    @Bind(R.id.chat_msg_view)
    TextView chatMsgView;

    ChatServer chatServer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        ButterKnife.bind(this);

        BusManager.getInstance().registerMainThreadListener(this);

        infoIpView.setText(IPUtils.getIpAddresses());

        // start chat server
        chatServer = new ChatServer(BuildConfig.SERVER_PORT, logger, broadcaster);
        chatServer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatServer.shutdown();
        BusManager.getInstance().unregisterMainThreadListener(this);
        ButterKnife.unbind(this);
    }

    @Subscribe
    public void onServerStarted(ServerStartedEvent event) {
        infoPortView.setText("I'm waiting here: " + event.serverPort);
    }

    @Subscribe
    public void onMessageSent(MessageSentEvent event) {
        chatMsgView.setText(event.message.toString());
    }

    private final Broadcaster broadcaster = new Broadcaster() {
        @Override
        public void broadcastMessage(Object message) {
            BusManager.getInstance().postInMainThread(message);
        }
    };

    private final Logger logger = new Logger() {
        @Override
        public void log(String tag, Throwable throwable) {
            Log.e(tag, throwable.getMessage());
        }
    };
}
