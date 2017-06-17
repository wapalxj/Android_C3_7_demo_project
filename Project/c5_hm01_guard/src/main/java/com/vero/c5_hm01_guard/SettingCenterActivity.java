package com.vero.c5_hm01_guard;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import Utils.MyConstants;
import Utils.ServiceUtils;
import Utils.SpTools;
import service.ComingPhoneService;
import service.TelSmsBlackService;
import view.SettingCenterItemView;

public class SettingCenterActivity extends AppCompatActivity {

    private SettingCenterItemView sciv_autoupdate;//自动更新
    private SettingCenterItemView sciv_blackservice;//黑名单拦截
    private SettingCenterItemView sciv_phonelocationService;//来电显示服务设置
    //归属地
    private TextView tv_ls_content;//归属地样式内容
    private ImageView iv_changeStyle;//归属地选择样式
    private Dialog dialog;//归属地对话框
    private RelativeLayout rl_location_root;//归属地样式根布局

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
        //初始化来电归属地服务cb初始值
        sciv_phonelocationService.setChecked(ServiceUtils.isServiceRunning(SettingCenterActivity.this, "service.ComingPhoneService"));

    }

    /**
     * 初始化事件
     */
    private String[] styleNames=new String[]{"卫士蓝","金属灰","苹果绿","半透明"};
    private void initEvent() {
        //归属地根布局事件
        rl_location_root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //点击根布局，iv图片也变化
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                         iv_changeStyle.setPressed(true);
                        break;
                    case MotionEvent.ACTION_UP:
                         iv_changeStyle.setPressed(false);
                        showStyleDialog();
                        break;
                }
                return false;
            }
        });

        //归属地iv事件
        iv_changeStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //归属地样式对话框显示
               showStyleDialog();
            }

        });

        //来电显示归属地服务启动或关闭
        sciv_phonelocationService.setItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断黑名单服务是否正在运行
                boolean isRunning=ServiceUtils.isServiceRunning(SettingCenterActivity.this, "service.ComingPhoneService");
                if(isRunning){
                    //关闭服务
                    Intent phonelocationService=new Intent(SettingCenterActivity.this, ComingPhoneService.class);
                    stopService(phonelocationService);
                    //设置复选框状态
                    sciv_phonelocationService.setChecked(false);
                }else {
                    //打开服务
                    Intent phonelocationService=new Intent(SettingCenterActivity.this,ComingPhoneService.class);
                    startService(phonelocationService);
                    //设置复选框状态
                    sciv_phonelocationService.setChecked(true);
                }
            }
        });
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

    //归属地样式对话框显示
    private void showStyleDialog() {
        //对话框让用户选择样式
        AlertDialog.Builder  ab=new AlertDialog.Builder(SettingCenterActivity.this);

        ab.setTitle("请选择归属地样式");
        ab.setSingleChoiceItems(
                styleNames,
                //参数2：默认值
                Integer.parseInt(SpTools.getString(getApplicationContext(),MyConstants.STYLEINDEX,"0")),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //which:选择的位置
                        //选择后文本变化
                        tv_ls_content.setText(styleNames[which]);
                        SpTools.putString(getApplicationContext(),MyConstants.STYLEINDEX,which+"");
                        dialog.dismiss();
                    }
                });
        dialog = ab.create();
        dialog.show();
    }

    private void initView() {
        setContentView(R.layout.activity_setting_center);
        //获取自动更新的自定义view
        sciv_autoupdate = (SettingCenterItemView) findViewById(R.id.sciv_setting_center_autoupdate);
        //获取黑名单拦截的自定义view
        sciv_blackservice= (SettingCenterItemView) findViewById(R.id.sciv_setting_blacksevice);
        //获取来电显示服务的设置
        sciv_phonelocationService= (SettingCenterItemView) findViewById(R.id.sciv_setting_phonelocationsevice);

        //归属地样式根布局
        rl_location_root = (RelativeLayout) findViewById(R.id.rl_setting_locationstyle_root);
        //归属地样式内容
        tv_ls_content = (TextView) findViewById(R.id.tv_setting_locationstyle_content);
        //归属地样式的按钮
        iv_changeStyle = (ImageView) findViewById(R.id.iv_setting_locationstyle_selector);

    }
}
