package com.example.test;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试发送短信
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView mTv_bro;
    Button btn_sendSms;
    Button btn_sendBro;
    private SmsReceiver mSmsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Log.e("sms", String.valueOf(getPackageManager().getPackageInfo(this.getPackageName(),0).applicationInfo.labelRes));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        checkAPI();
        setContentView(R.layout.activity_main);
        initView();
        initReceiver();
    }

    private void checkAPI() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions=new ArrayList<>();
            int sendSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            int readSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
            int receiveSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
            if(sendSmsPermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.SEND_SMS);
            }
            if(readSmsPermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_SMS);
            }
            if(receiveSmsPermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.RECEIVE_SMS);
            }
            if(permissions.size()>0){
                ActivityCompat.requestPermissions(this,permissions.toArray(new String[permissions.size()]),1);
            }
        }
    }

    private void initReceiver() {
        mSmsReceiver = new SmsReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        filter.addAction(Telephony.Sms.Intents.SMS_DELIVER_ACTION);
        registerReceiver(mSmsReceiver,filter);
    }

    private void initView() {
        mTv_bro= (TextView) findViewById(R.id.tv_bro);
        btn_sendBro= (Button) findViewById(R.id.btn_sendSmsBro);
        btn_sendSms= (Button) findViewById(R.id.btn_sendSms);
        btn_sendSms.setOnClickListener(this);
        btn_sendBro.setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSmsReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sendSms:
                //发送短信
                SmsManager sm=SmsManager.getDefault();
                sm.sendTextMessage("+8618468050252","","aaaaaaa",null,null);
                break;
            case R.id.btn_sendSmsBro:
                //发送短信广播
                Intent intent=new Intent("com.vero.vnix");
                sendBroadcast(intent);
                break;
             default:
                break;
        }
    }

    /**
     * 接收短信广播
     */
    class SmsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction().toString();
            Log.e("action",""+action);
            mTv_bro.setText(action);
        }
    }
}
