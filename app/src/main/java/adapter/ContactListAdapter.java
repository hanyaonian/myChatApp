package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dell.wilddogchat.R;

import java.util.List;

/**
 * Created by DELL on 2017/7/13.
 */

public class ContactListAdapter extends BaseAdapter{
    //最好是排过序的list
    private List<String> contactList;
    private Context context;
    public ContactListAdapter(Context Appcontext, List<String> friendList) {
        this.context = Appcontext;
        this.contactList = friendList;
    }
    @Override
    public int getCount() {
        if(contactList == null) return 0;
        return contactList.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        if (contactList == null) return 0;
        return contactList.get(position);
    }

    //根据app研发录内的指导写的getView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false);
            holder.userName = (TextView)convertView.findViewById(R.id.contact_friend_name);
            holder.userHeadImg = (ImageView)convertView.findViewById(R.id.userHeadImg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String userName = contactList.get(position);
        holder.userName.setText(userName);
        holder.userHeadImg.setImageResource(R.drawable.person);
        return convertView;
    }
    //使用holder可以避免频繁的创建项
    private class ViewHolder {
        TextView userName;
        ImageView userHeadImg;
    }
    public int getFirstLetterPos(char letter) {
        if(contactList != null) {
            for (int i = 0; i < contactList.size(); i++) {
                //返回首字母位置
                char f_c = contactList.get(i).charAt(0);
                if (letter == f_c) return i;
            }
            return -1;
        }
        return -1;
    }
}
