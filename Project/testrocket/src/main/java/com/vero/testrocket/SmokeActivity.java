package com.vero.testrocket;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import static com.vero.testrocket.R.id.iv_smoke_m;
import static com.vero.testrocket.R.id.iv_smoke_t;
//冒烟
public class SmokeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smoke);
        //烟柱
        ImageView iv_t= (ImageView) findViewById(iv_smoke_t);
        //烟座
        ImageView iv_m= (ImageView) findViewById(iv_smoke_m);

        //动画1.渐变2.比例
        AlphaAnimation aa=new AlphaAnimation(0.0f,1.0f);
        ScaleAnimation sa=new ScaleAnimation(0,1,0.0f,1.0f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1.0f);
        aa.setDuration(1000);
        sa.setDuration(1000);
        AnimationSet as=new AnimationSet(true);
        as.addAnimation(aa);
        as.addAnimation(sa);
        iv_m.startAnimation(aa);//底座只有alpha动画
        iv_t.startAnimation(as);//烟柱2种动画
        //2秒后关闭
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        }).start();
    }
}
