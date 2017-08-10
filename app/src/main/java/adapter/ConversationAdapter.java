package adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.dell.wilddogchat.R;
import com.github.library.bubbleview.BubbleTextView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.List;

import db.MyDb;

/**
 * Created by hanya on 2017/8/10.
 */

public class ConversationAdapter extends RecyclerView.Adapter implements View.OnClickListener{
    List<EMMessage> messages;
    private Context appContext;
    private MyDb db;
    private Bitmap myImg, friendImg;
    public ConversationAdapter(Context context, List<EMMessage> EMmessages) {
        messages = EMmessages;
        appContext = context;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //friend chat
        if ( !messages.get(position).getFrom().equals(EMClient.getInstance().getCurrentUser())) {
            ((MyViewHolder)holder).my_part.setVisibility(View.INVISIBLE);
            ((MyViewHolder)holder).friend_part.setVisibility(View.VISIBLE);
            if (friendImg != null) {
                ((MyViewHolder)holder).friend_headImg.setImageBitmap(friendImg);
            } else {
                ((MyViewHolder)holder).friend_headImg.setImageResource(R.drawable.person);
            }
            EMMessage temp = messages.get(position);
            if(temp.getType() == EMMessage.Type.TXT) {
                ((MyViewHolder) holder).friend_msg.setText(((EMTextMessageBody) temp.getBody()).getMessage());
            } else {
                ((MyViewHolder) holder).friend_msg.setText("我更新了头像~");
            }
        }
        //my chat
        else {
            ((MyViewHolder)holder).friend_part.setVisibility(View.INVISIBLE);
            ((MyViewHolder)holder).my_part.setVisibility(View.VISIBLE);
            if ( myImg != null) {
                ((MyViewHolder)holder).my_headImg.setImageBitmap(myImg);
            } else {
                ((MyViewHolder)holder).my_headImg.setImageResource(R.drawable.person);
            }
            EMMessage temp = messages.get(position);
            if(temp.getType() == EMMessage.Type.TXT) {
                ((MyViewHolder) holder).my_msg.setText(((EMTextMessageBody) temp.getBody()).getMessage());
            } else {
                ((MyViewHolder) holder).my_msg.setText("我更新了头像~");
            }
        }
        //将位置放在tag处，onclick时取出
        holder.itemView.setTag(position);
    }
    //点击事件
    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            onItemClickListener.onClick(v, (int)v.getTag());
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        //为每个item添加点击事件
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        if (messages == null) return 0;
        return messages.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public BubbleTextView my_msg, friend_msg;
        public ImageView my_headImg, friend_headImg;
        public RelativeLayout my_part, friend_part;
        public MyViewHolder(View view) {
            super(view);
            my_msg = (BubbleTextView) view.findViewById(R.id.conversation_my_msg);
            friend_headImg = (ImageView)view.findViewById(R.id.conversation_friend_headImg);
            friend_msg = (BubbleTextView)view.findViewById(R.id.conversation_friend_msg);
            my_headImg = (ImageView)view.findViewById(R.id.conversation_my_headImg);
            my_part = (RelativeLayout)view.findViewById(R.id.my_part);
            friend_part = (RelativeLayout)view.findViewById(R.id.friend_part);
        }
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }
    private OnItemClickListener onItemClickListener = null;
}
