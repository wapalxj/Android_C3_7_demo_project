package com.vero.c5_hm01_guard;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import Utils.MyConstants;
import Utils.ServiceUtils;
import Utils.SpTools;
import service.LostFindService;

public class Setup4Activity extends BaseSetupActivity {
    CheckBox cb_isProtected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化复选框CheckBox的值
     */
    @Override
    public void initData() {
        //初始化复选框CheckBox的值
        //如果服务开启就打钩，否则不打钩
        if(ServiceUtils.isServiceRunning(Setup4Activity.this,"service.LostFindService")){
            cb_isProtected.setChecked(true);
        }else {
            cb_isProtected.setChecked(false);
        }
        super.initData();

    }

    /**
     * 初始化复选框CheckBox事件
     */
    @Override
    public void initEvent() {
        super.initEvent();
        cb_isProtected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //如果选择打钩，开启防盗保护，防盗保护是一个服务
                if(isChecked){
                    //开机启动防盗
                    SpTools.putBoolean(getApplicationContext(),MyConstants.LOSTFIND,true);
                    Log.e("check-true","check-true");
                    //true,开启防盗保护
                    Intent intent=new Intent(Setup4Activity.this, LostFindService.class);
                    startService(intent);
                }else {
                    Log.e("check-false","check-false");
                    //关闭防盗保护
                    Intent intent=new Intent(Setup4Activity.this, LostFindService.class);
                    stopService(intent);
                }
            }
        });
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup4);
        //打钩开启防盗保护CheckBox
        cb_isProtected= (CheckBox) findViewById(R.id.cb_setup4_isProtected);
    }

    @Override
    protected void nextActivity() {
        //保存设置完成的状态
        SpTools.putBoolean(Setup4Activity.this, MyConstants.ISSETUP,true);
        //跳转到手机防盗界面
        startActivity(LostFindActivity.class);
    }

    @Override
    protected void prevActivity() {
        //跳转到第4个设置向导界面
        startActivity(Setup3Activity.class);
    }
}
