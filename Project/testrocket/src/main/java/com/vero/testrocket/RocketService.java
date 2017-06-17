package com.vero.testrocket;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class RocketService extends Service {
    private WindowManager.LayoutParams mParams;
    private View rocketView;
    private WindowManager wm;
    private float startX;
    private float startY;
    private float moveX;
    private float moveY;

    @Override
    public void onCreate() {
        super.onCreate();
        initToastParams();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        showRocket();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeRocket();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //关闭小火箭
    private void closeRocket(){
        //小火箭布局
        if (rocketView!=null){
            wm.removeView(rocketView);
        }
    }


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            wm.updateViewLayout(rocketView,mParams);
        }
    };
    //显示小火箭
    private void showRocket(){
        //小火箭布局
        rocketView = View.inflate(getApplicationContext(),
                R.layout.rocket,null);
        ImageView bg= (ImageView) rocketView.findViewById(R.id.iv_rocket);
        //获取小火箭的动画背景
        AnimationDrawable ad= (AnimationDrawable)
                bg.getBackground();
        //开始播放动画
        ad.start();
        //小火箭触摸事件,按住拖动---->松开发射火箭
        rocketView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
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
                        wm.updateViewLayout(rocketView,mParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        //判断位置，然后发射
                        //判断位置发射
                        if (mParams.x>100 &&
                                mParams.x+ v.getWidth()<wm.getDefaultDisplay().getWidth()-100
                                && mParams.y>200){
                            //x轴离2边框超过100,y轴方向大于200，则发射
                            //发射:1.火箭往上跑，2.烟雾
                            mParams.x=(wm.getDefaultDisplay().getWidth()-v.getWidth())/2;//中线发射
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int j=0;mParams.y>0;j+=5){
                                        SystemClock.sleep(30);
                                        mParams.y-=j;
                                        //更新UI，service没有runOnUI方法，使用handler
                                        handler.obtainMessage().sendToTarget();
                                    }
                                    SystemClock.sleep(100);
                                    stopSelf();
                                }
                            }).start();
                            //冒烟的Activity
                            Intent intent =new Intent(RocketService.this,SmokeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                        break;
                }
                return false;
            }
        });
        wm.addView(rocketView,mParams);
    }
    //初始化吐司参数
    private void initToastParams() {
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        //上面的默认TYPE_TOAST是不响应事件的
        mParams.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mParams.setTitle("Toast");
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.gravity= Gravity.LEFT|Gravity.TOP;
    }
}
