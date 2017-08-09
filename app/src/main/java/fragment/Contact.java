package fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.wilddogchat.R;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activity.conversation;
import adapter.ContactListAdapter;
import ui.IndexSlideBar;

import static com.hyphenate.chat.EMGCMListenerService.TAG;

//TODO:以后蛮闲的时候再把好友请求持久化，现在就先支持一个吧
public class Contact extends Fragment {
    private ListView contactList;
    private static final int GET_FRIEND_LIST = 1;
    private static final int FRIND_DELETED = -1;
    private static final int HEADER_COUNT = 2;
    private List<String> friend_list;
    private Map<String, String> new_friend;
    private ContactListAdapter adapter;
    private IndexSlideBar indexSlideBar;
    private TextView textDialog, invite_hint;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        friend_list = new ArrayList<>();//initial
        new_friend = new HashMap<>();
        getFriendList();
        //listener
        EMClient.getInstance().contactManager().setContactListener(contactListener);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().contactManager().removeContactListener(contactListener);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        bindView(view);
        return view;
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
    public void addHeaderView() {
        View contactList_header_new = LayoutInflater.from(getContext()).inflate(R.layout.contact_header_new, null);
        View contactList_header_add = LayoutInflater.from(getContext()).inflate(R.layout.contact_header_add, null);
        invite_hint = (TextView) contactList_header_new.findViewById(R.id.unread_new_friend_num);
        contactList.addHeaderView(contactList_header_add);
        contactList.addHeaderView(contactList_header_new);
    }
    public void bindView(View view) {
        contactList = (ListView)view.findViewById(R.id.contact_list);
        contactList.setOnItemClickListener(contact_item_click);
        contactList.setOnItemLongClickListener(contact_item_longClick);
        textDialog = (TextView)view.findViewById(R.id.text_dialog);
        indexSlideBar = (IndexSlideBar)view.findViewById(R.id.index_slideBar);
        indexSlideBar.setTextView(textDialog);
        //headerView
        addHeaderView();
        indexSlideBar.setOnTouchingLetterChangedListener(new IndexSlideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                if (adapter == null) return;
                int position = adapter.getFirstLetterPos(s.charAt(0));
                if (position != -1) {
                    contactList.setSelection(position+HEADER_COUNT);
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
                case FRIND_DELETED:
                    String friendName = (String) msg.obj;
                    friend_list.remove(friendName);
                    adapter.notifyDataSetChanged();
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
    public void showAddFriendDialog() {
        final View friend_name_text = LayoutInflater.from(getContext()).inflate(R.layout.add_friend_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("添加好友")
                .setIcon(R.drawable.add_user)
                .setView(friend_name_text)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //- -
                    }
                })
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //add friend
                        EditText name = (EditText) friend_name_text.findViewById(R.id.add_friend_name);
                        EditText reason = (EditText) friend_name_text.findViewById(R.id.add_friend_reason);
                        String friend_name = name.getText().toString();
                        String add_reason = reason.getText().toString();
                        addFriend(friend_name, add_reason);
                    }
                })
                .setCancelable(true);
        AlertDialog add_friend_dialog = builder.create();
        add_friend_dialog.show();
    }
    public void showNewFriendDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("添加好友")
                .setIcon(R.drawable.search_new)
                .setMessage(new_friend.get("name") + "说：" +new_friend.get("reason"))
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        invite_hint.setText("");
                        new_friend.clear();
                    }
                })
                .setPositiveButton("接受", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            EMClient.getInstance().contactManager().acceptInvitation(new_friend.get("name"));
                            invite_hint.setText("");
                            new_friend.clear();
                        } catch (HyphenateException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                });
        AlertDialog new_friend_dialog = builder.create();
        new_friend_dialog.show();
    }
    public void showMenuDialog(final String friendName) {
        final String[] items = {"与他聊天", "删除好友"};
        AlertDialog.Builder MenuDialog = new AlertDialog.Builder(getContext());
        MenuDialog
                .setCancelable(true)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                startChat(friendName);
                                break;
                            case 1:
                                deleteFriend(friendName);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .show();
    }
    public void deleteFriend(final String friendName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().deleteContact(friendName);
                    Message msg = new Message();
                    msg.obj = friendName;
                    msg.what = FRIND_DELETED;
                    handler.sendMessage(msg);
                } catch (HyphenateException e) {
                    Log.e("delete friend: ", e.getMessage());
                } catch (Exception e) {
                    Log.e("delete friend: ", e.toString());
                }
            }
        }).start();
    }
    public void startChat(String username) {
        Intent intent = new Intent(getActivity(), conversation.class);
        intent.putExtra("talkWithWho", username);
        startActivity(intent);
    }
    public void addFriend(String name, String reason) {
        try {
            EMClient.getInstance().contactManager().addContact(name, reason);
        }catch (HyphenateException e) {
            Log.e(e.getErrorCode()+":" , e.getMessage() );
        }
    }
    //listView Listener
    private AdapterView.OnItemClickListener contact_item_click = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                showAddFriendDialog();
            } else if (position == 1 && new_friend.size() != 0) {
                showNewFriendDialog();

            } else if (position == 1 && new_friend.size() == 0) {
                Toast.makeText(getContext(), "No new friend", Toast.LENGTH_SHORT).show();
            } else {
                if (friend_list.size() == 0) return;
               startChat(friend_list.get(position - HEADER_COUNT));
            }
        }
    };
    private AdapterView.OnItemLongClickListener contact_item_longClick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (position > 1 && friend_list.size() > 0) {
                showMenuDialog(friend_list.get(position - 2));
            }
            return true;
        }
    };
    //contact listener
    private EMContactListener contactListener = new EMContactListener() {
        @Override
        public void onContactAdded(String username) {
            //根据首字母插入新好友。。
            String Username = username.substring(0,1).toUpperCase() + username.substring(1);
            if (!friend_list.contains(Username))
                friend_list.add(getInsertPos(Username.charAt(0)), Username);
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onContactDeleted(String username) {
            //TODO:被删除时回调此方法
            if (friend_list.contains(username)) friend_list.remove(username);
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onContactInvited(String username, String reason) {
            //发起加为好友用户的名称
            //对方发起好友邀请时发出的文字性描述
            new_friend.put("name", username);
            new_friend.put("reason", reason);
            invite_hint.setText("新请求");
        }
        @Override
        public void onFriendRequestAccepted(String username) {
            //增加了联系人时回调此方法
            String Username = username.substring(0,1).toUpperCase() + username.substring(1);
            friend_list.add(getInsertPos(Username.charAt(0)), Username);
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onFriendRequestDeclined(String username) {
            //被拒绝时的方法
        }
    };
    public int getInsertPos(char x) {
        if (friend_list.size() > 0) {
            for (int i = 0; i < friend_list.size(); i++) {
                if (friend_list.get(i).charAt(0) > x) return i;
            }
        }
        return friend_list.size();
    }
}
