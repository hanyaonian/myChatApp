package activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dell.wilddogchat.R;
import com.hyphenate.chat.EMClient;

import adapter.FragmentAdapter;
import fragment.ChatFragment;

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

    }
    //测试用，用于取消自动登录
    @Override
    protected void onStop() {
        super.onStop();
       // EMClient.getInstance().logout(true);
    }
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
}
