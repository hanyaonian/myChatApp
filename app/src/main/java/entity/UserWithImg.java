package entity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.example.dell.wilddogchat.R;

/**
 * Created by DELL on 2017/7/21.
 */

public class UserWithImg {
    private Bitmap headImg;
    private String username;
    public UserWithImg(String name ,Bitmap img) {
        headImg = img;
        username = name;
    }
    public UserWithImg(String name) {
        username = name;
        headImg = null;
    }
}
