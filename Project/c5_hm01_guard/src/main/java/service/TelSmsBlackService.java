package service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.vero.c5_hm01_guard.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractList;
import java.util.Objects;

import dao.BlackDao;
import domain.BlackTable;

/**
 * 监听电话和短信的服务
 */
public class TelSmsBlackService extends Service {

    private SmsReceiver receiver;
    private BlackDao dao;
    private PhoneStateListener phoneStateListener;//监听电话的状态
    private TelephonyManager tm;

    @Override
    public void onCreate() {
        Log.e("ComingPhoneService","onCreate");
        //提高服务运行级别
        NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplication());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("vero_guard");
        builder.setContentText("vnix_guard");
        Intent intent=new Intent();
        intent.setAction("com.vero.guard.homeactivity");
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,0);
        builder.setContentIntent(pendingIntent);
        Notification notification=builder.build();
        startForeground(1,notification);
        //初始化黑名单业务类
        dao = new BlackDao(getApplicationContext());
        //注册短信监听--->广播
        receiver = new SmsReceiver();
        IntentFilter filter=new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver,filter);
        //注册电话监听
        //TelephonyManager
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //监听电话的状态
        phoneStateListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                //state:电话的状态
                //incomingNumber:打进来的号码
                switch (state){
                    case TelephonyManager.CALL_STATE_IDLE:
                        //挂断的状态
                        System.out.println("空闲的状态");
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        //响铃的状态
                        System.out.println("响铃的状态:"+incomingNumber);
                        //获取电话的模式
                        int mode=dao.getMode(incomingNumber);
                        if((mode & BlackTable.TEL)!=0){
                            //电话拦截
                            System.out.println("电话拦截:"+incomingNumber);
                            //挂断电话之前先注册内容观察者
                            getContentResolver().registerContentObserver(
                                    Uri.parse("content://call_log/calls"),
                                    true,
                                    new ContentObserver(new Handler()) {
                                @Override
                                public void onChange(boolean selfChange) {
                                    //电话日志变化 ，触发此方法
                                    //删除电话日志
                                    deleteCallLog(incomingNumber);
                                    //取消注册，不取消会删除非拦截号码
                                    getContentResolver().unregisterContentObserver(this);
                                    super.onChange(selfChange);
                                }
                            });
                            //挂断电话
                            endCall();

                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        //通话的状态
                        System.out.println("通话的状态:"+incomingNumber);
                        break;
                     default:
                        break;
                }
            }
        };
        //注册电话监听
        tm.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    /**
     * 删除电话日志
     * @param incomingNumber
     */
    private void deleteCallLog(String incomingNumber) {
        //只能通过内容提供者删除电话日志
        Uri uri= Uri.parse("content://call_log/calls");
        //删除日志
        getContentResolver().delete(uri,"number = ?",
                new String[]{incomingNumber});
    }

    /**
     * 挂断电话
     */
    private void endCall() {
//        tm.endCall();//android 1.5被阉割
        //ServiceManager,getService();
        //反射
        try {
            //1.获取class
            Class c=Class.forName("android.os.ServiceManager");
            //2.获取method
            Method method=c.getDeclaredMethod("getService",String.class);
            //3.产生obj--->不需要，因为调用的是静态方法
            //4.调用
            IBinder binder= (IBinder) method.invoke(null,Context.TELEPHONY_SERVICE);
            //5.aidl访问
            ITelephony iTelephony=ITelephony.Stub.asInterface(binder);
            //挂断电话
            iTelephony.endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        //取消短信监听
        unregisterReceiver(receiver);
        //取消电话监听
        tm.listen(phoneStateListener,PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    /**
     * 短信监听广播
     */
    private class SmsReceiver  extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] datas= (Object[]) intent.getExtras().get("pdus");
            for (Object sms:datas) {
                //获取短信数据
                SmsMessage sm=SmsMessage.createFromPdu((byte[]) sms);
                //获取短信号码
                String address=sm.getOriginatingAddress();
                //判断是否存在黑名单中
                int mode=dao.getMode(address);
                if((mode & BlackTable.SMS) !=0){
                    //具有短信拦截功能
                    //拦截此短信
                    Log.e("TelSmsBlackService","SmsReceiver-拦截此短信");
                    abortBroadcast();
                }
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
