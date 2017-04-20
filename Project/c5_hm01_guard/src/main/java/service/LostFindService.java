package service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.vero.c5_hm01_guard.R;

/**
 * 再服务中注册广播
 */
public class LostFindService extends Service {

    private SmsReceiver receiver;
    private boolean isPlaying;//正在播放音效
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
                    abortBroadcast();
                }else if(msg.equals("#*lockscreen*#")){
                    Log.e("LostFindService","lockscreen");
                    //手机发送短信#*lockscreen*#到丢失手机
                    //一键锁屏
                    DevicePolicyManager dpm= (DevicePolicyManager)
                            getSystemService(DEVICE_POLICY_SERVICE);
                    //设置密码
                    dpm.resetPassword("123",0);
                    dpm.lockNow();
                    abortBroadcast();
                }else if(msg.equals("#*wipedata*#")){
                    Log.e("LostFindService","wipedata");
                    //清除数据
                    DevicePolicyManager dpm= (DevicePolicyManager)
                            getSystemService(DEVICE_POLICY_SERVICE);
                    //清除SD卡数据
                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    abortBroadcast();
                }else if(msg.equals("#*music*#")){
                    Log.e("LostFindService","music");
                    //播放音效
                    //只播放一次(当值重叠)
                    MediaPlayer mp=
                            MediaPlayer.create(getApplicationContext(),R.raw.music);
                    if(!isPlaying){
                        //设置左右声道声音为最大值
                        mp.setVolume(1,1);
                        //播放
                        mp.start();
                        isPlaying=true;
                    }
                    //播放完成回调
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            //音乐播放完毕触发
                            isPlaying=false;
                        }
                    });
                    abortBroadcast();
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
