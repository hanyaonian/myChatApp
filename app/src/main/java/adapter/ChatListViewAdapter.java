package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.wilddogchat.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ChatListViewAdapter extends BaseAdapter {

    private final Map<String, EMConversation> conversations;
    private final Context Appcontext;
    private List<String> conversation_list;

    public ChatListViewAdapter(Context context,Map<String, EMConversation> Conversations, List<String> conversationList){
        conversations = Conversations;
        Appcontext = context;
        conversation_list = conversationList;
    }
    @Override
    public int getCount() {
        if (conversation_list != null) {
            return conversation_list.size();
        } else return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        EMConversation conversation = conversations.get(conversation_list.get(position));
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(Appcontext).inflate(R.layout.chat_item, parent, false);
            holder.friendHeadImg = (ImageView)convertView.findViewById(R.id.head_img);
            holder.friendName = (TextView)convertView.findViewById(R.id.chat_nickName);
            holder.lastestMsg = (TextView)convertView.findViewById(R.id.latest_message);
            holder.lastestDate = (TextView)convertView.findViewById(R.id.latest_message_date);
            holder.unread_message_num = (TextView)convertView.findViewById(R.id.unread_message_num);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
            holder.friendHeadImg.setImageResource(R.drawable.person);
            holder.friendName.setText(conversation_list.get(position));
            //如果没有发消息只是点开一个对话窗，那就不显示对话列表
            String lastestDate = DateUtils.getTimestampString(
                    new Date(conversation.getLastMessage().getMsgTime()));
            holder.lastestDate.setText(lastestDate);
            //set last msg
        if (conversation.getLastMessage().getType() == EMMessage.Type.IMAGE) {
            holder.lastestMsg.setText("提示：更新了头像哦");
        } else {
            String haha = conversation.getLastMessage().getBody().toString();
            holder.lastestMsg.setText(haha.split("\"")[1]);
        }
        handleUnreadMsg(holder.unread_message_num, conversation);
        return convertView;
    }
    public void handleUnreadMsg(View view, EMConversation conversation) {
        if (conversation.getUnreadMsgCount() > 0) {
            int num = conversation.getUnreadMsgCount();
            view.setVisibility(View.VISIBLE);
            ((TextView)view).setText(num+"");
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public Object getItem(int position) {
            return conversations.get(conversation_list.get(position));
    }
    class ViewHolder {
        TextView friendName, lastestMsg, lastestDate, unread_message_num;
        ImageView friendHeadImg;
    }
}
