package com.vero.c5_hm01_guard;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import Utils.MD5Utils;
import Utils.MyConstants;
import Utils.SpTools;

public class HomeActivity extends AppCompatActivity {
    private GridView mGridView_menus;
    private AlertDialog mDialog;
    private int[] icons={R.mipmap.safe,R.mipmap.callmsgsafe,R.drawable.item_gv_selector_app,
                        R.mipmap.taskmanager,R.mipmap.netmanager,R.mipmap.trojan,
                        R.mipmap.sysoptimize,R.mipmap.atools,R.mipmap.settings};
    private String names[]={"手机防盗","通讯卫士","软件管家",
                            "进程管理","流量统计","病毒查杀",
                             "缓存清理","高级工具","设置中心",};
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        checkAPI();
        initData();
        initEvent();
    }

    /**
     * 初始化组件事件
     */
    private void initEvent() {
        mGridView_menus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0://手机防盗
                        //自定义对话框
                        //先判断是否已经设置过密码
                        if(TextUtils.isEmpty(SpTools.getString(HomeActivity.this,MyConstants.PASSWORD,""))){
                            showSettingPassDialog();
                        }else {
                            //已设置过则为登录对话框
                            showEnterPassDialog();
                        }

                        break;
                    case 1:
                        break;
                     default:
                        break;
                }
            }
        });
    }

    /**
     * 输入密码(防盗登录)的对话框
     */
    private void showEnterPassDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.dialog_enter_password,null);
        final EditText et_pwd1= (EditText) view.findViewById(R.id.et_dialog_enter_pwd_one);
        Button btn_setPass= (Button) view.findViewById(R.id.btn_dialog_enter_login);
        Button btn_cancel= (Button) view.findViewById(R.id.btn_dialog_enter_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        btn_setPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置密码
                String pwd_1=et_pwd1.getText().toString().trim();
                if(TextUtils.isEmpty(pwd_1)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    //密码判断，MD5两次加密
                    pwd_1= MD5Utils.md5(MD5Utils.md5(pwd_1));
                    //读取SP中保存的密文，进行判断
                    if(pwd_1.equals(SpTools.getString(HomeActivity.this,MyConstants.PASSWORD,""))){
                        //密码正确
                        //进入手机防盗界面
                        Intent i=new Intent(HomeActivity.this,LostFindActivity.class);
                        startActivity(i);
                    }else {
                        //密码不正确
                        Toast.makeText(HomeActivity.this,"密码不正确",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    SpTools.putString(HomeActivity.this, MyConstants.PASSWORD,pwd_1);
                    mDialog.dismiss();
                }
            }
        });
        builder.setView(view);
        mDialog=builder.create();
        mDialog.show();
    }
    /**
     * 设置密码的对话框
     */
    private void showSettingPassDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(this,R.layout.dialog_setting_password,null);
        final EditText et_pwd1= (EditText) view.findViewById(R.id.et_dialog_setting_pwd_one);
        final EditText et_pwd2= (EditText) view.findViewById(R.id.et_dialog_setting_pwd_two);
        Button btn_setPass= (Button) view.findViewById(R.id.btn_dialog_setting_pwd);
        Button btn_cancel= (Button) view.findViewById(R.id.btn_dialog_setting_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        btn_setPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置密码
                String pwd_1=et_pwd1.getText().toString().trim();
                String pwd_2=et_pwd2.getText().toString().trim();
                if(TextUtils.isEmpty(pwd_1)||TextUtils.isEmpty(pwd_2)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }else if(!pwd_1.equals(pwd_2)){
                    Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    //保存密码
                    Log.e("pwd_dialg","保存密码");
                    //md5加密:进行2次加密
                    pwd_1= MD5Utils.md5(MD5Utils.md5(pwd_1));
                    SpTools.putString(HomeActivity.this, MyConstants.PASSWORD,pwd_1);
                    mDialog.dismiss();
                }
            }
        });
        builder.setView(view);
        mDialog=builder.create();
        mDialog.show();
    }

    /**
     * 初始化组件数据
     */
    private void initData() {
        mAdapter = new MyAdapter();
        mGridView_menus.setAdapter(mAdapter);
    }

    private void initView() {
        mGridView_menus= (GridView) findViewById(R.id.home_gridview);

    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(HomeActivity.this,R.layout.item_home_gridview,null);
            ImageView imageView= (ImageView) view.findViewById(R.id.iv_item_home_gv_icon);
            TextView textView= (TextView) view.findViewById(R.id.tv_item_home_gv_name);

            imageView.setImageResource(icons[position]);
            textView.setText(names[position]);
            return view;
        }
    }

    /**
     * 动态权限
     */
    private void checkAPI() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            int checkReadContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED || checkReadContactsPermission != PackageManager.PERMISSION_GRANTED ){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS},1);
            }

        }
    }
}
