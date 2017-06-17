package com.vero.testrocket;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
//小火箭效果
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //6.0以上对应的开启SYSTEM_OVERLAY_WINDOW权限
        //这里用于来电归属地显示的自定义Toast
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }
    //点击按钮打开小火箭
    public void openRocket(View view){
        Intent i=new Intent(MainActivity.this,RocketService.class);
        startService(i);
        finish();
    }
}