package com.vero.c5_hm01_guard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

/**
 * 第一个设置向导界面
 */
public class Setup1Activity extends BaseSetupActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void initView() {

    }

    @Override
    protected void nextActivity() {
        //跳转到底2个设置向导界面
        startActivity(Setup2Activity.class);
    }

    @Override
    protected void prevActivity() {
        //前面没有设置向导界面了
    }

}
