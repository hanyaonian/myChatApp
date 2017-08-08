package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.dell.wilddogchat.R;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import activity.conversation;
import adapter.ChatListViewAdapter;

public class ChatFragment extends Fragment {
    private static final int MESSAGE_RECIEVE = 1;
    private static final int MESSAGE_SEND = 2;
    private ChatListViewAdapter chatListViewAdapter;
    private ListView chatList;
    private Map<String, EMConversation> conversations;
    private List<String> conversation_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpListener();
        updateList();
    }

    public void initVar() {
        conversation_list = new ArrayList<>();
        for (Map.Entry<String, EMConversation> entry : conversations.entrySet()) {
            conversation_list.add(entry.getKey());
        }
        chatList.setOnItemLongClickListener(chatItemLongClick);
        chatList.setOnItemClickListener(chatItemClick);
    }
    public void bindView(View view) {
        chatList = (ListView)view.findViewById(R.id.chat_list);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        bindView(view);
        getChatList();
        return view;
    }
    //获取对话列表
    public void getChatList() {
        conversations = EMClient.getInstance().chatManager().getAllConversations();
        //设置好对话名列表
        initVar();
        //排序
        sortList();
        chatListViewAdapter = new ChatListViewAdapter(getContext(), conversations, conversation_list);
        chatList.setAdapter(chatListViewAdapter);
    }
    public void setUpListener() {
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    }
    private EMMessageListener emMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //收到消息
            Message msg = new Message();
            msg.what = MESSAGE_RECIEVE;
            handler.sendMessage(msg);
        }
        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {

        }
        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //发出消息
            Message msg = new Message();
            msg.what = MESSAGE_SEND;
            handler.sendMessage(msg);
        }
        @Override
        public void onMessageDelivered(List<EMMessage> messages) {

        }
        @Override
        public void onMessageChanged(EMMessage message, Object change) {

        }
    };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_RECIEVE:
                    updateList();
                    break;
                case MESSAGE_SEND:
                    updateList();
                    break;
                default:
                    break;
            }
        }
    };

    //排序一下聊天顺序
    public void sortList() {
        Collections.sort(conversation_list, new Comparator<String>() {
            @Override
            public int compare(String user1, String user2) {
                if (conversations.get(user1).getLastMessage().getMsgTime() > conversations.get(user2).getLastMessage().getMsgTime())
                    return -1;
                else
                    return 1;
            }
        });
    }
    public void updateList() {
        conversations = EMClient.getInstance().chatManager().getAllConversations();
        if (conversations.size() > conversation_list.size()) {
            getChatList();
        } else {
            //one
            synchronized (this) {
                //列表数量少时直接再排吧，如果数量多，改成将收到消息的人挪到第一位去
                sortList();
                chatListViewAdapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        EMClient.getInstance().chatManager().removeMessageListener(emMessageListener);
    }
    private AdapterView.OnItemClickListener chatItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), conversation.class);
            intent.putExtra("talkWithWho", conversation_list.get(position));
            startActivity(intent);
        }
    };
    //TODO: 删除聊天记录
    private AdapterView.OnItemLongClickListener chatItemLongClick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            return false;
        }
    };
}
