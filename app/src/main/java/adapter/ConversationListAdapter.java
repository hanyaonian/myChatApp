package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.dell.wilddogchat.R;
import com.github.library.bubbleview.BubbleTextView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

/**
 * Created by DELL on 2017/7/18.
 */

public class ConversationListAdapter extends BaseAdapter {
    List<EMMessage> messages;
    private Context appContext;
   public ConversationListAdapter(List<EMMessage> EMmessages, Context context) {
       appContext = context;
       messages = EMmessages;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getCount() {
        if(messages != null) {
            return messages.size();
        }
        else return 0;
    }
    @Override
    public Object getItem(int position) {
        if(messages != null)
            return messages.get(position);
        else return null;
    }
    //TODO:shit, bug here,好像这种模式不一样的不能考虑复用
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        //if (convertView == null) {
            holder = new ViewHolder();
            if ( !messages.get(position).getFrom().equals(EMClient.getInstance().getCurrentUser())) {
                convertView = LayoutInflater.from(appContext).inflate(R.layout.conversation_item_friend, parent, false);
                holder.headImg = (ImageView) convertView.findViewById(R.id.conversation_friend_headImg);
                holder.msg = (BubbleTextView) convertView.findViewById(R.id.conversation_friend_msg);
            } else {
                convertView = LayoutInflater.from(appContext).inflate(R.layout.conversation_item_my, parent, false);
                holder.headImg = (ImageView) convertView.findViewById(R.id.conversation_my_headImg);
                holder.msg = (BubbleTextView)convertView.findViewById(R.id.conversation_my_msg);
             //   }
            //   convertView.setTag(holder);
        } //else {
            //holder = (ViewHolder) convertView.getTag();
       // }
        //set views
        EMMessage temp = messages.get(position);
        holder.msg.setText(((EMTextMessageBody)temp.getBody()).getMessage());
        holder.headImg.setImageResource(R.drawable.person);
        return convertView;
    }
    class ViewHolder {
        BubbleTextView msg;
        ImageView headImg;
    }
}
