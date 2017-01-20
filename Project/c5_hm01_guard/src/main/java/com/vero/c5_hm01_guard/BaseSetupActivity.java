package com.vero.c5_hm01_guard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016/12/5.
 */

public abstract class BaseSetupActivity extends AppCompatActivity {
    GestureDetector gd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        iniGesture();//初始化手势识别器

        initData();//初始化数据
        initEvent();//初始化组件事件


    }




    public abstract void initView();

    //下一步按钮事件
    public void next(View view){
        //1，界面的切换
        //2,动画的播放
        nextActivity();
        nextAnimation();

    }
    public void initData() {

    }

    public void initEvent() {

    }
    //上一步按钮事件
    public void prev(View view){
        //1，界面的切换
        //2,动画的播放
        prevActivity();
        prevAnimation();
    }
    //共有的界面跳转方法
    public void startActivity(Class type){
        Intent next=new Intent(this,type);
        startActivity(next);
        finish();
    }

    protected abstract void nextActivity();
    protected abstract void prevActivity();
    /**
     * 界面切换动画:"下一步"动画
     */
    private void nextAnimation() {
        //参数1：in的动画
        //参数2：out的动画
        overridePendingTransition(R.anim.next_in,R.anim.next_out);
    }
    /**
     * 界面切换动画:"上一步"动画
     */
    private void prevAnimation() {
        //参数1：in的动画
        //参数2：out的动画
        overridePendingTransition(R.anim.prev_in,R.anim.prev_out);
    }

    /**
     * 手势识别器
     */
    private void iniGesture() {
        //初始化GestureDetector,下一步：需要绑定onTouchEvent事件
        gd=new GestureDetector(new GestureDetector.OnGestureListener() {

            /**覆盖此方法完成手势切换效果
             * e1:按下的点
             * e2:松开的点
             * velocityX:x轴方向的速度--->px/s
             * velocityY:y轴方向的速度--->px/s
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //X轴方向的速度是否满足横向滑动条件
                if(velocityX > 200){//速度大于400px/s,满足滑动条件
                    //判断间距
                    int dx= (int) (e2.getX()-e1.getX());//X轴方向滑动间距
                    if(Math.abs(dx) < 100){//间距不符合，直接无效
                        return true;
                    }
                    if(dx<0){//从右往左
                        next(null);//--->不是通过组件(button)调用的传null
                    }else {//从左往右
                        prev(null);
                    }
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }


        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gd.onTouchEvent(event);
    }
}
