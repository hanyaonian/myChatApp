package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.dell.wilddogchat.R;
import com.hyphenate.chat.EMClient;

import entity.User;

public class SplashActivity extends Activity {
    private final int sleepTime = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (EMClient.getInstance().isLoggedInBefore()) {
            EMClient.getInstance().chatManager().getAllConversations();
            EMClient.getInstance().groupManager().getAllGroups();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    {
                        if (User.getInstance().isLogin() == false) {
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                            }
                            startActivity(new Intent(SplashActivity.this, userlogin.class));
                            finish();
                        }
                    }
                }
            }).start();
        }
    }
}
