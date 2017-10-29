package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.vero.c5_hm01_guard.R;

import Utils.MyConstants;
import Utils.SpTools;
import engine.PhoneLocationEngine;

/**
 * 主要用于监控来电电话，并且显示归属地
 */
public class ComingPhoneService extends Service {

    private PhoneStateListener listener;
    private TelephonyManager tm;
    private WindowManager.LayoutParams mParams;
    private WindowManager mWM;//用来设置Toast的view
    private View mView;//Toast的view
    private float startX;
    private float startY;
    private float moveX;
    private float moveY;
    private OutCallReceiver outCallReceiver;

    @Override
    public void onCreate() {
        Log.e("ComingPhoneService","onCreate");
        //外拨电话
        outCallReceiver = new OutCallReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(outCallReceiver,filter);
        //初始化吐司参数
        initToastParams();
        //WindowManager mWM;//用来设置Toast的view
        mWM = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
        //初始化电话状态监听
        //获取电话管理器
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        listener = new PhoneStateListener(){
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                switch (state){
                    case TelephonyManager.CALL_STATE_IDLE://空闲，挂断,初始执行
                        //关闭吐司
                        closeLocationToast();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
                        //关闭吐司
                        closeLocationToast();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING://响铃
                        //显示吐司
                        showLocationToast(incomingNumber);
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }
        };
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }
    //初始化吐司参数
    private void initToastParams() {
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        //上面的默认TYPE_TOAST是不响应事件的
        mParams.type=WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mParams.setTitle("Toast");
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //初始坐标，记录坐标之后才添加的，不想使用上一次的记录坐标，可以不写
        //先设置对齐方式为坐上角对齐
        mParams.gravity= Gravity.LEFT|Gravity.TOP;
        mParams.x= (int) Float.parseFloat(SpTools.getString(getApplicationContext(),MyConstants.TOAST_X,"0"));
        mParams.y= (int) Float.parseFloat(SpTools.getString(getApplicationContext(),MyConstants.TOAST_Y,"0"));
    }

    /**
     * 显示吐司
     * @param incomingNumber
     */
    int bgStyles[]=new int[]{R.mipmap.call_locate_blue,R.mipmap.call_locate_gray,R.mipmap.call_locate_green,R.mipmap.call_locate_white};

    private void showLocationToast(String incomingNumber) {
        //如果先后2个电话打进来，先关闭前一个，在显示后一个
        closeLocationToast();
        //吐司显示的view
        mView=View.inflate(this, R.layout.sys_toast,null);
        //设置Toast背景
        int index=Integer.parseInt(SpTools.getString(getApplicationContext(),MyConstants.STYLEINDEX,"0"));
        mView.setBackgroundResource(bgStyles[index]);

        TextView tv_location= (TextView) mView.findViewById(R.id.tv_toast_location);
        tv_location.setText(PhoneLocationEngine.locationQuery(incomingNumber,getApplicationContext()));
        //拖动吐司：初始化view的触摸事件
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("onTouch",event.getX()+":"+event.getRawX());
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //记录X,Y坐标
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //新的X,Y坐标
                        moveX = event.getRawX();
                        moveY = event.getRawY();
                        //拖动距离dx  dy
                        float dx=moveX-startX;
                        float dy=moveY-startY;
                        //改变土司的坐标
                        mParams.x+=dx;
                        mParams.y+=dy;
                        //重新获取新的X,Y坐标
                        startX = moveX;
                        startY = moveY;
                        //更新吐司的位置
                        mWM.updateViewLayout(mView,mParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        //防止越界
                        if (mParams.x<0){
                            mParams.x=0;
                        }else if (mParams.x+mView.getWidth()>mWM.getDefaultDisplay().getWidth()){
                            mParams.x=mWM.getDefaultDisplay().getWidth()-mView.getWidth();
                        }
                        if (mParams.y<0){
                            mParams.y=0;
                        }else if (mParams.y+mView.getHeight()>mWM.getDefaultDisplay().getHeight()){
                            mParams.y=mWM.getDefaultDisplay().getHeight()-mView.getHeight();
                        }
                        //记录当前吐司位置,把坐标保存到SP
                        SpTools.putString(getApplicationContext(), MyConstants.TOAST_X,mParams.x+"");
                        SpTools.putString(getApplicationContext(), MyConstants.TOAST_Y,mParams.y+"");
                        break;
                }
                return false;
            }
        });

        mWM.addView(mView, mParams);
    }

    private Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closeLocationToast();
        }
    };
    /**
     * 关闭吐司
     */
    private void closeLocationToast() {
        //Toast是加载到windowmanager中的，window不关闭，Toast就不会关闭，所以需要我们主动关闭
        if (iSoutCall){
            //是外拨电话
            //延迟关闭
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //3秒后关闭
                    SystemClock.sleep(3000);
                    handler.obtainMessage().sendToTarget();
                }
            }).start();
            iSoutCall=false;
        }else {
            //接听电话直接关闭
            if (mView!=null){
                mWM.removeView(mView);
                mView = null;
            }
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        //注销监听
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        //注销外拨广播
        unregisterReceiver(outCallReceiver);
        super.onDestroy();
    }

    /**
     * 电话外拨的广播
     */
    private boolean iSoutCall=false;//是否外拨电话
    private class OutCallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取外拨电话号码
            String phoneNumber=getResultData();
            showLocationToast(phoneNumber);
            iSoutCall=true;//因为showLocationToast中第一行代码为关闭Toast，所以这个标记只能在showLocationToast后面执行
        }
    }
}
