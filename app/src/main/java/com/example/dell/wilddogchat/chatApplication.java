package com.example.dell.wilddogchat;

import android.app.Application;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;


/**
 * Created by DELL on 2017/7/10.
 */

public class chatApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EMOptions options = new EMOptions();
        //需要好友验证
        options.setAcceptInvitationAlways(false);
        options.setAutoLogin(true);
        EMClient.getInstance().init(getApplicationContext(), options);
        EMClient.getInstance().setDebugMode(true);
    }
}
