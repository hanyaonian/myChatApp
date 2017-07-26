package activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by DELL on 2017/7/11.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        initViews(savedInstanceState);
        initData();
    }
    protected abstract void initViews(Bundle savedInstanceState);
    protected abstract void initData();
    protected abstract void initVariables();
}
