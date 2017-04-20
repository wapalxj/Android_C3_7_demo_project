package com.vero.c5_hm01_guard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import Utils.MyConstants;
import Utils.ServiceUtils;
import Utils.SpTools;
import service.TelSmsBlackService;
import view.SettingCenterItemView;

public class SettingCenterActivity extends AppCompatActivity {

    private SettingCenterItemView sciv_autoupdate;//自动更新
    private SettingCenterItemView sciv_blackservice;//黑名单拦截
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
        initData();
    }

    /**
     * 初始化组件数据
     */
    private void initData() {
        //初始化自动更新cb初始值
        sciv_autoupdate.setChecked(SpTools.getBoolean(getApplicationContext(), MyConstants.AUTOUPDATE, false));
        //初始化黑名单拦截cb初始值
        sciv_blackservice.setChecked(ServiceUtils.isServiceRunning(SettingCenterActivity.this, "service.TelSmsBlackService"));
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //自动更新的事件处理
        sciv_autoupdate.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sciv_autoupdate.setChecked(!sciv_autoupdate.isChecked());
                //如果复选框选中，自动更新开启，否则不开启
                //记录复选框状态
                SpTools.putBoolean(getApplicationContext(), MyConstants.AUTOUPDATE, sciv_autoupdate.isChecked());
            }
        });
        //黑名单服务的启动和关闭
        sciv_blackservice.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断黑名单服务是否正在运行
                boolean isRunning=ServiceUtils.isServiceRunning(SettingCenterActivity.this, "service.TelSmsBlackService");
                if(isRunning){
                    //关闭服务
                    Intent balckservice=new Intent(SettingCenterActivity.this,TelSmsBlackService.class);
                    stopService(balckservice);
                    //设置复选框状态
                    sciv_blackservice.setChecked(false);
                }else {
                    //打开服务
                    Intent balckservice=new Intent(SettingCenterActivity.this,TelSmsBlackService.class);
                    startService(balckservice);
                    //设置复选框状态
                    sciv_blackservice.setChecked(true);
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_setting_center);
        //获取自动更新的自定义view
        sciv_autoupdate = (SettingCenterItemView) findViewById(R.id.sciv_setting_center_autoupdate);
        //获取黑名单拦截的自定义view
        sciv_blackservice= (SettingCenterItemView) findViewById(R.id.sciv_setting_blacksevice);
    }
}
