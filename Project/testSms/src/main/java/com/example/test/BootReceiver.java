package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

/**
 * 开机启动的广播接收者,魅族5.0，华为荣耀8 6.0真机测试失败，模拟器测试成功
 */
public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String s=intent.getAction().toString();
        SmsManager sm=SmsManager.getDefault();
        sm.sendTextMessage("+8618468050252","","wo shi xiao tou---"+s,null,null);
    }
}
