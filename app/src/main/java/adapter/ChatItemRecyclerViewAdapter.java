package adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dell.wilddogchat.R;
import com.example.dell.wilddogchat.dummy.ChatContent.ChatItem;

import java.util.List;

public class ChatItemRecyclerViewAdapter extends RecyclerView.Adapter<ChatItemRecyclerViewAdapter.ViewHolder> {

    private final List<ChatItem> mValues;

    public ChatItemRecyclerViewAdapter(List<ChatItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        //holder.mIdView.setText(mValues.get(position).id);
        //holder.mContentView.setText(mValues.get(position).content);

        holder.thisView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View thisView;
        public final TextView lastestMessage, lastestMessage_date, chat_nickName;
        public ChatItem mItem;

        public ViewHolder(View view) {
            super(view);
            thisView = view;
            lastestMessage = (TextView) view.findViewById(R.id.latest_message);
            chat_nickName = (TextView) view.findViewById(R.id.chat_nickName);
            lastestMessage_date = (TextView)view.findViewById(R.id.latest_message_date);
        }
    }
}
