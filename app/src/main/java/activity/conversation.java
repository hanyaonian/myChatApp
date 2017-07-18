package activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.dell.wilddogchat.R;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

import adapter.ConversationListAdapter;

public class conversation extends BaseActivity {
    private ListView message_list;
    List<EMMessage> messages;
    private EMConversation conversation;
    private EditText input_box;
    private Button send_butt;
    private String talkToWho;
    private ConversationListAdapter conversationListAdapter;
    private static final int MESSAGE_RECIEVE = 1;
    private static final int MESSAGE_SEND = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_conversation);
        message_list = (ListView)findViewById(R.id.messagelist);
        input_box = (EditText)findViewById(R.id.message_content);
        send_butt = (Button)findViewById(R.id.send_msg_butt);
        send_butt.setOnClickListener(send_msg_butt_click);
    }
    @Override
    protected void initVariables() {
        talkToWho = getIntent().getStringExtra("talkWithWho").toLowerCase();
        setTitle("与 "+talkToWho+ " 对话中");
    }
    @Override
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
                conversationListAdapter.notifyDataSetChanged();
                message_list.setSelection(messages.size()-1);
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
        conversationListAdapter = new ConversationListAdapter(messages, getApplicationContext());
        message_list.setAdapter(conversationListAdapter);
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
                    conversationListAdapter.notifyDataSetChanged();
                    message_list.setSelection(messages.size()-1);
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
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
    }
}
