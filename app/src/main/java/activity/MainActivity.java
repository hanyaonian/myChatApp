package activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.wilddogchat.R;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adapter.FragmentAdapter;

public class MainActivity extends BaseActivity{
    private ViewPager viewPager;
    private FragmentAdapter fragmentAdapter;
    private ViewPager.OnPageChangeListener listenerOnPage;
    private final static int CHAT_POS = 0;
    private final static int CONTACT_POS = 1;
    private final static int PERSON_POS = 2;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }

    };
    //初始化数据
    @Override
    protected void initData() {
    }
    //初始化views
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        //set up views
        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        //set up adapter
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        //监听滑动
        listenerOnPage = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //...
            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case CHAT_POS:
                        navigation.setSelectedItemId(R.id.navigation_home);
                        break;
                    case CONTACT_POS:
                        navigation.setSelectedItemId(R.id.navigation_dashboard);
                        break;
                    case PERSON_POS:
                        navigation.setSelectedItemId(R.id.navigation_notifications);
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                // ...
            }
        };
        viewPager.setOnPageChangeListener(listenerOnPage);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.setCurrentItem(0);
    }
    //初始化变量
    @Override
    protected void initVariables() {
        setListener();
    }
    public void setListener() {
        EMClient.getInstance().chatManager().addMessageListener(listener);
    }
    public void showNotification(List<EMMessage> messages) {
        Set<String> users = new HashSet<>();
        String notifyBody = "";
        for (int i = 0; i < messages.size(); i++) {
            users.add(messages.get(i).getFrom());
        }
        if (users.size() == 1) {
            if (messages.get(0).getType() == EMMessage.Type.TXT)
                notifyBody = messages.get(0).getFrom() + "说: " + ((EMTextMessageBody)(messages.get(0).getBody())).getMessage();
             else return;
        } else {
            notifyBody = " 有" + users.size() + "位联系人发了消息";
        }
        //pending intent
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        //show notify
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("有新消息")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(notifyBody)
                .setContentIntent(intent)
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);
    }
    EMMessageListener listener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            showNotification(messages);
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

    //双击退出
    private long[] mHits = new long[2];
    @Override
    public void onBackPressed() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 2000)) {// 2000代表设定的间隔时间
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "再次返回退出", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(listener);
    }
}
