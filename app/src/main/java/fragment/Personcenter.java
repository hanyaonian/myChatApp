package fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.wilddogchat.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import activity.MainActivity;
import activity.userlogin;
import db.MyDb;


public class Personcenter extends Fragment {
    private Button setMessageListener, setNickName, logout;
    private ImageView setHeadImg;
    private TextView nickName;
    private MyDb db;
    private static final String FILE_PATH = "/mychatApp_headImg";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new MyDb(getContext(), "db", null, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personcenter, container, false);
        bindView(view);
        return view;
    }
    public void bindView(View view) {
        setHeadImg = (ImageView) view.findViewById(R.id.head_img_butt);
        setMessageListener = (Button)view.findViewById(R.id.center_setting_listen);
        setNickName = (Button)view.findViewById(R.id.center_set_nickname);
        logout = (Button)view.findViewById(R.id.center_log_out);
        //从db获取当前用户头像
        if(db.getBitmap(EMClient.getInstance().getCurrentUser()) != null) {
            setHeadImg.setImageBitmap(db.getBitmap(EMClient.getInstance().getCurrentUser()));
        }
        setHeadImg.setOnClickListener(setHeadImg_click);
        nickName = (TextView)view.findViewById(R.id.center_nick_name);
        nickName.setText(EMClient.getInstance().getCurrentUser());
        logout.setOnClickListener(logout_click);
    }
    private View.OnClickListener setHeadImg_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            choseImgFromGallery();
        }
    };
    //TODO:回头看看这是啥
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_REQUEST_RESULT = 0xa2;
    private static final int CODE_REQUEST_CANCELD = 162;
    //从相册中选取照片
    public void choseImgFromGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, CODE_GALLERY_REQUEST);
        Toast.makeText(getContext(), "从相册中选取图片", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CODE_REQUEST_CANCELD) {
            return;
        }
        else if (requestCode == CODE_GALLERY_REQUEST) {
            if (data != null) {
                cutPhoto(data.getData());
            }
        } else if (requestCode == CODE_REQUEST_RESULT) {
            //set photo
            setMyHeadImg(data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void setMyHeadImg(Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Bitmap img = intent.getParcelableExtra("data");
                if (db.getBitmap(EMClient.getInstance().getCurrentUser()) != null) {
                    //更新数据库
                    db.replaceHeadImg(EMClient.getInstance().getCurrentUser(), img);
                } else {
                    //存入
                    db.insert(EMClient.getInstance().getCurrentUser(), img);
                }
                //sendNewHeadImgToEveryOne(img);
                setHeadImg.setImageBitmap(img);
            }
        }
    }
    public void cutPhoto(Uri uri) {
        Intent cutIntent = new Intent("com.android.camera.action.CROP");
        cutIntent.setDataAndType(uri, "image/*");
        //设置裁剪
        cutIntent.putExtra("crop", "true");
        //设置高宽比例
        cutIntent.putExtra("aspectX", 1);
        cutIntent.putExtra("aspectY", 1);
        //裁剪高宽,px为单位
        cutIntent.putExtra("outputX", 150);
        cutIntent.putExtra("outputY", 150);
        cutIntent.putExtra("return-data", true);

        startActivityForResult(cutIntent, CODE_REQUEST_RESULT);
    }
    public void sendNewHeadImgToEveryOne(final Bitmap image) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> usernames;
                saveBitmapToLocal(image);
                try {
                    usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    Message msg = new Message();
                    msg.what = SEND_ALL_HEADIMG;
                    msg.obj = usernames;
                    handler.sendMessage(msg);
                } catch (HyphenateException e) {
                    Log.e("send all", e.getMessage());
                }
            }
        }).start();
    }
    private void saveBitmapToLocal(Bitmap bitmap) {
        File file = new File(getContext().getFilesDir()+FILE_PATH);
        FileOutputStream out;
        if (file.exists()) {
            file.delete();
        } else {
            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)){
                    out.flush();
                    out.close();
                }
            }catch (FileNotFoundException fe) {
                Log.e("saveBitmap", fe.toString());
            }catch (IOException ie) {
                Log.e("saveBitmap", ie.toString());
            }
        }
    }
    private static final int SEND_ALL_HEADIMG = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEND_ALL_HEADIMG:
                    List<String> usernames = (List<String>) msg.obj;
                    File file = new File(getContext().getFilesDir()+FILE_PATH);
                    if (file.exists()) {
                        for (int i = 0; i < usernames.size(); i++) {
                            //发送图片信息
                            EMMessage message = EMMessage.createImageSendMessage(getContext().getFilesDir() + FILE_PATH, false, usernames.get(i));
                            EMClient.getInstance().chatManager().sendMessage(message);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };
    private View.OnClickListener logout_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EMClient.getInstance().logout(true);
            getActivity().finish();
            Intent intent = new Intent(getActivity(), userlogin.class);
            startActivity(intent);
        }
    };
}
