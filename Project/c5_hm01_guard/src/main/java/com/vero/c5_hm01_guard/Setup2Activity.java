package com.vero.c5_hm01_guard;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import Utils.MyConstants;
import Utils.SpTools;

public class Setup2Activity extends BaseSetupActivity {
    private Button bt_bind;
    private ImageView iv_isBind;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *初始化数据
     */
    @Override
    public void initData() {
        //图标数据初始化
        if(TextUtils.isEmpty(SpTools.getString(Setup2Activity.this, MyConstants.SIM,""))){
            //没有绑定SIM卡的图标
            iv_isBind.setImageResource(R.drawable.unlock);
        }else {
            iv_isBind.setImageResource(R.drawable.lock);
        }
    }
    @Override
    public void initView() {
        setContentView(R.layout.activity_setup2);
        //获取bind sim卡按钮
        bt_bind= (Button) findViewById(R.id.bt_setup2_bindsim);
        //是否为绑定sim卡图标
        iv_isBind=(ImageView) findViewById(R.id.iv_setup2_isbind);

    }

    /**
     * 添加自己的事件
     */
    @Override
    public void initEvent() {
        bt_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //绑定/解绑SIM卡
                if(TextUtils.isEmpty(SpTools.getString(Setup2Activity.this, MyConstants.SIM,""))){
                    //没有绑定SIM卡
                    //绑定SIM卡，存储SIM卡信息
                    //获取SIM卡信息
                    TelephonyManager tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber=tm.getSimSerialNumber();
                    //保存SIM卡信息
                    SpTools.putString(Setup2Activity.this, MyConstants.SIM,simSerialNumber);
                    //切换是否绑定SIM卡的图标
                    iv_isBind.setImageResource(R.drawable.lock);
                }else {
                    //绑定SIM卡，则解绑:设置为空即可
                    SpTools.putString(Setup2Activity.this, MyConstants.SIM,"");
                    //切换是否绑定SIM卡的图标
                    iv_isBind.setImageResource(R.drawable.unlock);
                }
            }
        });
        super.initEvent();
    }



    @Override
    public void next(View view) {
        if(TextUtils.isEmpty(SpTools.getString(Setup2Activity.this, MyConstants.SIM,""))){
            Toast.makeText(this, "请先绑定SIM卡", Toast.LENGTH_SHORT).show();
            return;
        }
        super.next(view);//调用到本类nextActivity()
    }

    @Override
    protected void nextActivity() {
        //跳转到第3个设置向导界面
        //没有绑定SIM卡，不能下一步:看本类next方法
        startActivity(Setup3Activity.class);
    }

    @Override
    protected void prevActivity() {
        //跳转到第1个设置向导界面
        startActivity(Setup1Activity.class);
    }
}
