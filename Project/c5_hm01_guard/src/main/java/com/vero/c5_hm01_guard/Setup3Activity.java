package com.vero.c5_hm01_guard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import Utils.EncrypyTools;
import Utils.MyConstants;
import Utils.SpTools;
import service.LocationService;

public class Setup3Activity extends BaseSetupActivity {
    private EditText et_safeNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initData() {
        super.initData();
        //获取并解密安全号码
        String safeNum = SpTools.getString(Setup3Activity.this, MyConstants.SAFENUMBER, "");
        safeNum=EncrypyTools.decrypt(MyConstants.ENCRYSEED,safeNum);
        et_safeNumber.setText(safeNum);
    }

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup3);
        //安全号码EditText
        et_safeNumber= (EditText) findViewById(R.id.et_setup3_safeNumber);
    }

    //按钮：选择安全号码
    //从手机联系人里获取
    public void selectSafeNumber(View view){
        //弹出新的Activity显示所有联系人的信息
        Intent friends =new Intent(this,FriendsActivity.class);
        startActivityForResult(friends,1);
        //获取Activity数据

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){//用户选择数据来关闭联系人界面，而不是点击返回键
            //取数据
            String phone=data.getStringExtra(MyConstants.SAFENUMBER);
            //显示安全号码
            et_safeNumber.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *
     * @param view
     */
    @Override
    public void next(View view) {
        //保存安全号码
        //如果安全号码不存在，不能下一步
        String safeNum=et_safeNumber.getText().toString().trim();
        if(TextUtils.isEmpty(safeNum)){
            Toast.makeText(this, "安全号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }else {
            //加密并保存安全号码
            safeNum= EncrypyTools.encrypt(MyConstants.ENCRYSEED,safeNum);
            SpTools.putString(this, MyConstants.SAFENUMBER,safeNum);
        }
        super.next(view);//继续咯
    }

    @Override
    protected void nextActivity() {
        //跳转到第4个设置向导界面
        startActivity(Setup4Activity.class);
    }

    @Override
    protected void prevActivity() {
        //跳转到第42个设置向导界面
        startActivity(Setup2Activity.class);
    }
}
