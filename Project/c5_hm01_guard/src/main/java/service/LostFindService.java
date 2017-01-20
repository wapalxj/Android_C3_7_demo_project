package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ObbInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * 再服务中注册广播
 */
public class LostFindService extends Service {

    private SmsReceiver receiver;

    public LostFindService() {
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 短信的广播接受者
     */
    private class SmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //短信拦截的功能
            Log.e("sm.getDisplayOriAddr","短信来了");
            Bundle extras=intent.getExtras();
            Object datas[]= (Object[]) extras.get("pdus");
            for (Object data : datas) {
                SmsMessage sm=SmsMessage.createFromPdu((byte [])data);
                Log.e("sm.getDisplayOriAddr",sm.getMessageBody()+":"+sm.getOriginatingAddress());
                String msg=sm.getMessageBody();
                if(msg.equals("#*gps*#")){
                    //安全手机发送短信#gps#到丢失手机
                    //耗时操作：定位，放到服务中
                    Log.e("LostFindService","gpsService");
                    Intent service =new Intent(context,LocationService.class);
                    startService(service);//启动定位的服务
                }

            }
        }
    }
    @Override
    public void onCreate() {
        //注册短信监听广播
        receiver = new SmsReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        filter.addAction(Telephony.Sms.Intents.SMS_DELIVER_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        //级别一样的时候，清单文件Manifest中，谁先注册谁先执行，
        // 如果级别一样，代码比Manifest中的更先执行
        registerReceiver(receiver,filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //取消注册短信的广播
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
