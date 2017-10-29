package com.vero.c5_hm01_guard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import engine.SmsEngine;

/**
 * 高级工具：电话归属地查询，短信备份和还原，程序锁的设置
 * 2个对话框都显示
 */
public class AToolActivity extends AppCompatActivity {
    private ProgressDialog pd;
    private ProgressBar pb_bk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        pd=new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }

    //归属地查询
    public void phoneQuery(View view){
        Intent intent=new Intent(this,PhoneLocationActivity.class);
        startActivity(intent);
    }

    //短信的备份
    public void smsBaike(View view){
        //传入2个进度条
        SmsEngine.smsBaikeJson(this, new SmsEngine.BaikeProgress() {
            @Override
            public void show() {
                pd.show();
                pb_bk.setVisibility(View.VISIBLE);
            }

            @Override
            public void setMax(int max) {
                pd.setMax(max);
                pb_bk.setMax(max);
            }
            @Override
            public void setProgress(int progress) {
                pd.setProgress(progress);
                pb_bk.setProgress(progress);
            }

            @Override
            public void end() {
                pd.setProgress(0);
                pb_bk.setProgress(0);
                pd.dismiss();
                pb_bk.setVisibility(View.GONE);
            }
        });
    }
    //短信的还原
    public void smsResume(View view){
        SmsEngine.smsResumeJson(this, new SmsEngine.BaikeProgress() {
            @Override
            public void show() {
                pd.show();
                pb_bk.setVisibility(View.VISIBLE);
            }

            @Override
            public void setMax(int max) {
                pd.setMax(max);
                pb_bk.setMax(max);
            }

            @Override
            public void setProgress(int progress) {
                pd.setProgress(progress);
                pb_bk.setProgress(progress);
            }

            @Override
            public void end() {
                pd.setProgress(0);
                pb_bk.setProgress(0);
                pd.dismiss();
                pb_bk.setVisibility(View.GONE);
            }
        });
    }
    private void initView() {
        setContentView(R.layout.activity_atool);
        pb_bk = (ProgressBar) findViewById(R.id.pb_smsbeike_progress);
    }

    public void lockActivity(View view){
        Intent intent=new Intent(this,LockedActivity.class);
        startActivity(intent);
    }
}
