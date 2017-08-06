package com.example.dell.wilddogchat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import activity.MainActivity;

public class MessageReceiveService extends Service {
    public MessageReceiveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        setListener();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        EMClient.getInstance().chatManager().removeMessageListener(listener);
        //stopSelf();
        super.onDestroy();
    }
    public void showNotification(List<EMMessage> messages) {
        Set<String> users = new HashSet<>();
        String notifyBody = "";
        for (int i = 0; i < messages.size(); i++) {
            users.add(messages.get(i).getFrom());
        }
        if (users.size() == 1) {
            if (messages.get(0).getType() == EMMessage.Type.TXT)
                notifyBody = messages.get(0).getFrom() + "说: " + ((EMTextMessageBody)(messages.get(0).getBody())).getMessage();
            else return;
        } else {
            notifyBody = " 有" + users.size() + "位联系人发了消息";
        }
        //pending intent
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        //show notify
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("有新消息")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(notifyBody)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);
    }
    EMMessageListener listener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            showNotification(messages);
        }
        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {

        }
        @Override
        public void onMessageRead(List<EMMessage> messages) {

        }
        @Override
        public void onMessageDelivered(List<EMMessage> messages) {

        }
        @Override
        public void onMessageChanged(EMMessage message, Object change) {

        }
    };
    public void setListener() {
        EMClient.getInstance().chatManager().addMessageListener(listener);
    }
}
