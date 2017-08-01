package activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.wilddogchat.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class userlogin extends BaseActivity {

    private Button login_butt, go_sign_up_butt;
    private EditText username_text, password_text;
    private View.OnClickListener login_butt_click, go_sign_up_butt_click;
    private Dialog dialog;

    @Override
    protected void initVariables() {
        //初始化点击事件
        login_butt_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        };
        go_sign_up_butt_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userlogin.this, signup.class);
                startActivity(intent);
            }
        };
    }

    @Override
    protected void initData() {

    }

    public void Login(){
        String username, password;
        username = username_text.getText().toString();
        password = password_text.getText().toString();
        if (username.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(), R.string.empty_hint, Toast.LENGTH_LONG).show();
        } else {
            userlogin(username, password);
        }
    }
    //需要一个加载dialog，不然会有傻逼一直点登录
    public void userlogin(final String username, final String password){
        dialog = ProgressDialog
                .show(this, "登录中", "正在进行连接", true);
        EMClient.getInstance().login(username, password, new EMCallBack(){
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "login", Toast.LENGTH_SHORT).show();
                    }
                });
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Intent intent = new Intent(userlogin.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            @Override
            public void onProgress(int progress, String status) {

            }
            @Override
            public void onError(int code, final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), getString(R.string.login_failed) + error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        //content view
        setContentView(R.layout.activity_userlogin);
        //set up views
        login_butt = (Button)findViewById(R.id.login_butt);
        go_sign_up_butt = (Button)findViewById(R.id.go_sign_up_butt);
        username_text = (EditText)findViewById(R.id.input_username);
        password_text = (EditText)findViewById(R.id.input_password);
        //set onclick listener
        login_butt.setOnClickListener(login_butt_click);
        go_sign_up_butt.setOnClickListener(go_sign_up_butt_click);
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

    @Override
    protected void onStop() {
        super.onStop();
        //EMClient.getInstance().logout(true);
        //if (dialog.isShowing()) dialog.dismiss();
    }
}