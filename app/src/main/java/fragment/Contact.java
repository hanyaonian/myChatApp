package fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dell.wilddogchat.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adapter.ContactListAdapter;
import ui.IndexSlideBar;

public class Contact extends Fragment {
    private ListView contactList;
    private static final int GET_FRIEND_LIST = 1;
    private List<String> friend_list;
    private ContactListAdapter adapter;
    private IndexSlideBar indexSlideBar;
    private TextView textDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFriendList();
    }
    //将list首字母转为大写
    public List<String> makeFirstUpperCase(List<String> list) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String temp = list.get(i).substring(0,1).toUpperCase() + list.get(i).substring(1);
            stringList.add(temp);
        }
        return stringList;
    }
    //根据首字母大写排序
    public List<String> sortListByLetter(List<String> names) {
        List<String> sorted_list = makeFirstUpperCase(names);
        Collections.sort(sorted_list);
        return sorted_list;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        bindView(view);
        return view;
    }
    public void bindView(View view) {
        contactList = (ListView)view.findViewById(R.id.contact_list);
        textDialog = (TextView)view.findViewById(R.id.text_dialog);
        indexSlideBar = (IndexSlideBar)view.findViewById(R.id.index_slideBar);
        indexSlideBar.setTextView(textDialog);

        indexSlideBar.setOnTouchingLetterChangedListener(new IndexSlideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getFirstLetterPos(s.charAt(0));
                if (position != -1) {
                    contactList.setSelection(position);
                }
            }
        });
    }
    //get contact list
    private void getFriendList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> usernames;
                try {
                    usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    Message msg = new Message();
                    msg.what = GET_FRIEND_LIST;
                    msg.obj = usernames;
                    handler.sendMessage(msg);
                }catch (HyphenateException e) {
                    Log.e("login:friend list", e.getMessage());
                }
            }
        }).start();
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GET_FRIEND_LIST:
                    List<String> temp = (List<String>) msg.obj;
                    setContactList(temp);
                    break;
                default:
                    break;
            }
        }
    };
    public void setContactList(List<String> temp) {
        friend_list = sortListByLetter(temp);
        adapter = new ContactListAdapter(getActivity().getApplicationContext(), friend_list);
        contactList.setAdapter(adapter);
    }
}
