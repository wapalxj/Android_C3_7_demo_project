package com.vero.c5_hm01_guard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import Utils.MyConstants;
import Utils.SpTools;

public class LostFindActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        //如果第一次进入该界面，则要先进入向导界面
        if(SpTools.getBoolean(this, MyConstants.ISSETUP,false)){
            //进入过向导界面，直接显示本界面
            initView();
        }else {
            //进入设置向导
            Intent i=new Intent(this,Setup1Activity.class);
            startActivity(i);
            finish();//关闭自己
        }

    }

    private void initView() {
        setContentView(R.layout.activity_lost_find);
    }

    /**
     * //重新进入设置向导
     * @param view
     */
    public void enterSetup(View view){
        Intent i=new Intent(this,Setup1Activity.class);
        startActivity(i);
        finish();//关闭自己
    }
}
