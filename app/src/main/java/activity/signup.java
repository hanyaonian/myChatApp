package activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dell.wilddogchat.R;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class signup extends BaseActivity {
    private EditText username_text, password_text, confirm_text, nickname_text;
    private Button sign_up_butt;
    private View.OnClickListener sign_up_butt_click;
    private Dialog dialog;

    @Override
    protected void initVariables() {
        sign_up_butt_click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, password, confirm;
                username = username_text.getText().toString();
                password = password_text.getText().toString();
                confirm = confirm_text.getText().toString();
                //检测输入有效性
                if (checkValid(username, password, confirm)) {
                    dialog = ProgressDialog
                            .show(signup.this, "注册中", "正在进行连接", false);
                    SignUp(username, password);
                }
            }
        };
    }
    public boolean checkValid(final String username, final String password, final String confirm) {
        if (!password.equals(confirm)) {
            Toast.makeText(getApplicationContext(), R.string.confirm_failed, Toast.LENGTH_LONG).show();
            return false;
        } else if (password.equals("") || confirm.equals("") || username.equals("")) {
            Toast.makeText(getApplicationContext(), R.string.empty_hint, Toast.LENGTH_LONG).show();
            return false;
        } else if (password.length() <= 3) {
            Toast.makeText(getApplicationContext(), R.string.too_short_hint, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    public void SignUp(final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(username, password);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                }catch (final HyphenateException e) {
                    //处理注册失败部分参考环信demo
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int errorCode = e.getErrorCode();
                            if(errorCode== EMError.NETWORK_ERROR){
                                Toast.makeText(getApplicationContext(), "当前网络不佳", Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ALREADY_EXIST){
                                Toast.makeText(getApplicationContext(), "该用户名已存在", Toast.LENGTH_SHORT).show();
                            }else if(errorCode == EMError.USER_ILLEGAL_ARGUMENT){
                                Toast.makeText(getApplicationContext(), "用户名不规范",Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(getApplicationContext(), "注册失败原因：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }
    @Override
    protected void initViews(Bundle savedInstanceState) {
        setContentView(R.layout.activity_signup);
        //set up views
        username_text = (EditText)findViewById(R.id.sign_up_username);
        password_text = (EditText)findViewById(R.id.sign_up_password);
        nickname_text = (EditText)findViewById(R.id.sign_up_nickname);
        confirm_text = (EditText)findViewById(R.id.sign_up_password_confirm);
        sign_up_butt = (Button)findViewById(R.id.sign_up_butt);
        sign_up_butt.setOnClickListener(sign_up_butt_click);
    }

    @Override
    protected void initData() {

    }
}
