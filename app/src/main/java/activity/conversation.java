package activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dell.wilddogchat.R;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

import adapter.ConversationAdapter;
import db.MyDb;
import service.MessageReceiveService;
import ui.SwipeableActivity;

public class conversation extends SwipeableActivity {
    private RecyclerView message_list;
    List<EMMessage> messages;
    private EMConversation conversation;
    private EditText input_box;
    private Button send_butt;
    private String talkToWho;
    private ConversationAdapter conversationAdapter;
    private static final int MESSAGE_RECIEVE = 1;
    private static final int MESSAGE_SEND = 2;
    private MessageReceiveService.chatBinder binder;
    private MyDb db;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MessageReceiveService.chatBinder) service;
            binder.getName(talkToWho);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //do nothing
        }
    };

    //TODO:aysnc 加载数据库图片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //返回按键
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initVariables();
        initViews(savedInstanceState);
        initData();
        //获取数据库
        db = new MyDb(getApplicationContext(), "db", null, 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //监听消息
        bindSerive();
    }

    public void bindSerive() {
        Intent intent = new Intent(this, MessageReceiveService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    //返回按键监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_conversation);
        message_list = (RecyclerView) findViewById(R.id.messagelist);
        input_box = (EditText)findViewById(R.id.message_content);
        send_butt = (Button)findViewById(R.id.send_msg_butt);
        send_butt.setOnClickListener(send_msg_butt_click);
    }
    protected void initVariables() {
        talkToWho = getIntent().getStringExtra("talkWithWho").toLowerCase();
        setTitle("与 "+talkToWho+ " 对话中");
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(talkToWho);
        //指定会话消息未读数清零
        conversation.markAllMessagesAsRead();
    }
    protected void initData() {
        conversation =  EMClient.getInstance().chatManager().getConversation(talkToWho, EMConversation.EMConversationType.Chat, false);
        if (conversation != null) {
            conversation.loadMoreMsgFromDB(conversation.getLastMessage().getMsgId(), 50);
        }
        setUpList();
    }
    private View.OnClickListener send_msg_butt_click = new View.OnClickListener() {
        @Override
        //发送消息
        public void onClick(View v) {
            String content = input_box.getText().toString();
            input_box.setText("");
            if (content.equals("")) return;
            else {
                EMMessage message = EMMessage.createTxtSendMessage(content, talkToWho);
                EMClient.getInstance().chatManager().sendMessage(message);
                messages.add(message);
                conversationAdapter.notifyDataSetChanged();
                message_list.smoothScrollToPosition(messages.size()-1);
            }
        }
    };
    public void setUpList() {
        //TODO:fix bug
        if (conversation != null) {
            messages = conversation.getAllMessages();
        } else {
            messages = new ArrayList<>();
        }
        conversationAdapter = new ConversationAdapter(getApplicationContext(), messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        message_list.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);
        message_list.setAdapter(conversationAdapter);
    }

    private EMMessageListener messageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            for (int i = 0; i < messages.size(); i++) {
                EMMessage message = messages.get(i);
                String cool = message.getFrom().toLowerCase();
                if (cool.equals(talkToWho)) {
                    Message msg = new Message();
                    msg.what = MESSAGE_RECIEVE;
                    msg.obj = message;
                    handler.sendMessage(msg);
                }
                if (message.getType() == EMMessage.Type.IMAGE) {
                    String update_user = message.getFrom();
                    String fileURL = ((EMImageMessageBody)message.getBody()).getLocalUrl();
                    Bitmap bitmap= BitmapFactory.decodeFile(fileURL);
                    db.insert(update_user, bitmap);
                }
            }
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
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_RECIEVE:
                    EMMessage recieved_msg = (EMMessage) msg.obj;
                    messages.add(recieved_msg);
                    conversationAdapter.notifyDataSetChanged();
                    break;
                case MESSAGE_SEND:
                    break;
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (conversation != null) {
            messages = conversation.getAllMessages();
            EMClient.getInstance().chatManager().importMessages(messages);
        }
        unbindService(connection);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }
}
