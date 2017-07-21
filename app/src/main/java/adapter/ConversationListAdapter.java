package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.dell.wilddogchat.R;
import com.github.library.bubbleview.BubbleTextView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

import db.MyDb;

/**
 * Created by DELL on 2017/7/18.
 */

public class ConversationListAdapter extends BaseAdapter {
    List<EMMessage> messages;
    private Context appContext;
    private MyDb db;
    private Bitmap myImg, friendImg;
   public ConversationListAdapter(List<EMMessage> EMmessages, Context context) {
       appContext = context;
       messages = EMmessages;
       db = new MyDb(appContext, "db", null, 1);
       initBitmap();
    }
    public void initBitmap() {
        //空对话不加载
        myImg = db.getBitmap(EMClient.getInstance().getCurrentUser());
        if (messages.size() == 0) return;

        if (messages.get(0).getFrom().equals(EMClient.getInstance().getCurrentUser())) {
            friendImg = db.getBitmap(messages.get(0).getTo());
        } else {
            friendImg = db.getBitmap(messages.get(0).getFrom());
        }
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
            holder = new ViewHolder();
            if ( !messages.get(position).getFrom().equals(EMClient.getInstance().getCurrentUser())) {
                convertView = LayoutInflater.from(appContext).inflate(R.layout.conversation_item_friend, parent, false);
                holder.headImg = (ImageView) convertView.findViewById(R.id.conversation_friend_headImg);
                holder.msg = (BubbleTextView) convertView.findViewById(R.id.conversation_friend_msg);
                if (friendImg != null) {
                    holder.headImg.setImageBitmap(friendImg);
                } else {
                    holder.headImg.setImageResource(R.drawable.person);
                }
            } else {
                convertView = LayoutInflater.from(appContext).inflate(R.layout.conversation_item_my, parent, false);
                holder.headImg = (ImageView) convertView.findViewById(R.id.conversation_my_headImg);
                holder.msg = (BubbleTextView) convertView.findViewById(R.id.conversation_my_msg);
                if (myImg != null) {
                    holder.headImg.setImageBitmap(myImg);
                } else {
                    holder.headImg.setImageResource(R.drawable.person);
                }
            }
            //TODO:这里急需优化，否则每个list读写数据库会爆炸
        EMMessage temp = messages.get(position);
        if(temp.getType() == EMMessage.Type.TXT) {
            holder.msg.setText(((EMTextMessageBody) temp.getBody()).getMessage());
        } else {
            holder.msg.setText("我更新了头像~");
        }
        return convertView;
    }
    class ViewHolder {
        BubbleTextView msg;
        ImageView headImg;
    }
}
