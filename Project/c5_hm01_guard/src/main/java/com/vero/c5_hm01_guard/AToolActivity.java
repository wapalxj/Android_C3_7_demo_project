package com.vero.c5_hm01_guard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import engine.SmsEngine;

/**
 * 高级工具：电话归属地查询，短信备份和还原，程序锁的设置
 */
public class AToolActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    //归属地查询
    public void phoneQuery(View view){
        Intent intent=new Intent(this,PhoneLocationActivity.class);
        startActivity(intent);
    }

    //短信的备份
    public void smsBaike(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SmsEngine.smsBaike(getApplicationContext());
            }
        }).start();
    }
    private void initView() {
        setContentView(R.layout.activity_atool);

    }
}
