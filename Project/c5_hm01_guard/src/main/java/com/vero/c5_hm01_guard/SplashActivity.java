package com.vero.c5_hm01_guard;
/**
 * Splash
 */

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Utils.MyConstants;
import Utils.SpTools;
import domain.UrlBean;

public class SplashActivity extends AppCompatActivity {

    private static final int LOADHOME = 1;//加载主界面
    private static final int SHOWUPDATEDIALOG = 2;//显示更新对话框
    private static final int ERROR = 3;//错误的统一代号
    private RelativeLayout mRoot;
    private int versionCode;
    private String versionName;
    private TextView versionTv;
    private UrlBean parsedJson;//解析到的JSON
    private long startTime;
    private long endTime;
    private ProgressBar pb_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAPI();
        initView();
        initData();
        initAnimation();
        //拷贝数据库--手机归属地
        copyDB("address.db");
        //拷贝数据库--病毒数据库
        copyDB("antivirus.db");
    }

    //拷贝文件
    private void copyDB(final String dbName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //判断文件是否已经存在
                File file=new File("/data/data/"+getPackageName()+"/files/"+"/"+dbName);
                if(file.exists()){
                    //文件已经存在
                    return;
                }
                try {
                    copyFile(dbName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 把assets目录下的数据库文件拷贝到本地
     *
     * @param dbName:文件名
     */
    private void copyFile(String dbName) throws IOException {
        //IO流
        AssetManager assetManager=getAssets();
        //读取asset的文件，转换成InputStream
        InputStream is=assetManager.open(dbName);
        //输出流
        FileOutputStream fos=openFileOutput(dbName,MODE_PRIVATE);

        //流的拷贝
        //定义缓冲区
        byte[] buffer=new byte[10240];

        //读取的长度
        int len =-1;
        int counts=1;//计数器
        //循环读取
        while ((len=is.read(buffer))!=-1){
            //把缓冲区的数据，写到输出流
            fos.write(buffer,0,len);
            //每100K刷新一次数据
            if(counts % 10 ==0){
                fos.flush();
            }
            counts++;
        }
        fos.flush();
        fos.close();
        is.close();
    }

    /**
     * 耗时的功能封装，只要是耗时的处理，都放进来
     */
    private void timeInit(){
        if(SpTools.getBoolean(getApplicationContext(), MyConstants.AUTOUPDATE,false)){
            //动画播放开始
            //检测版本更新
            //判断是否进行服务器版本的检测
            checkVersion();
        }
    }
    private void initData() {
        //获取自己的版本信息
        PackageManager pm=getPackageManager();
        try {
            PackageInfo packageInfo=pm.getPackageInfo(getPackageName(),0);
            //版本号,版本名
            versionCode=packageInfo.versionCode;
            versionName=packageInfo.versionName;
            //设置在界面上
            versionTv.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initView(){
        setContentView(R.layout.activity_splash);
        mRoot = (RelativeLayout) findViewById(R.id.splash_root);
        versionTv= (TextView) findViewById(R.id.tv);
        pb_download= (ProgressBar) findViewById(R.id.pb_download);

    }
    private void initAnimation() {
        AnimationSet sets=new AnimationSet(true);
        //动画
        Animation alpha =new AlphaAnimation(0.0f,1.0f);
//        RotateAnimation rotate=new RotateAnimation(0f,360f, Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        ScaleAnimation scale=new ScaleAnimation(0.0f,1.0f,0.0f,1.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        sets.addAnimation(alpha);
//        sets.addAnimation(rotate);
        sets.addAnimation(scale);
        sets.setDuration(3000);
        sets.setFillAfter(true);
        //设置动画完成的事件监听
        sets.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //动画一开始，就应该初始化数据(耗时操作：网络，本地数据初始化，数据copy等)
                timeInit();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //动画播放结束
                //不做版本检测的时候则直接进入主界面，否则将操作交给自动更新执行
                if(!SpTools.getBoolean(getApplicationContext(), MyConstants.AUTOUPDATE,false)){
                    loadHome();
                }else {
                    //界面的衔接是自动更新，再此不做处理
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //显示动画
        mRoot.startAnimation(sets);
    }

    /**
     * //访问服务器，获取数据url
     * 并且将错误统一处理
     */
    private void checkVersion(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                BufferedReader reader=null;
                HttpURLConnection conn=null;
                int errorCode=-1;//正常，没有错误
                try {
                    //开始的时间
                    startTime = System.currentTimeMillis();
                    URL url=new URL("http://10.0.2.2:8080/guard.json");
                    conn= (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(8000);
                    conn.setRequestMethod("GET");
                    int respCode=conn.getResponseCode();
                    if (respCode==200){
                        InputStream is=conn.getInputStream();
                        reader=new BufferedReader(new InputStreamReader(is));
                        String line=reader.readLine();
                    StringBuilder jsonStr=new StringBuilder();
                    while (line!=null){
                        jsonStr.append(line);
                        line=reader.readLine();
                    }
                    Log.e("jsonString",jsonStr.toString());
                    //解析JSON
                    parsedJson=parseJson(jsonStr);
                    Log.e("jsonBean","version:"+parsedJson.getVersionCode());

                    }else {
                        Log.e("respCode",respCode+"");
                            errorCode=404;
                    }
                } catch (MalformedURLException e) {
                    errorCode=4002;
                    Log.e("MalformedURLException","URL格式异常");
                    e.printStackTrace();
                } catch (IOException e) {
                    errorCode=4001;
                    Log.e("IOException","网络连接异常");
                    e.printStackTrace();
                } catch (JSONException e) {
                    errorCode=4003;
                    Log.e("JSONException","JSON格式异常");
                    e.printStackTrace();
                }finally {
//                    if (errorCode==-1){
//                        isNewVersion(parsedJson);
//                    }else {
//                        //弹出更新窗口
//                        Message msg=Message.obtain();
//                        msg.what=ERROR;
//                        msg.arg1=errorCode;
//                        handler.sendMessage(msg);
//                    }
                    Message msg=Message.obtain();
                    if (errorCode==-1){
                        //访问正常的时候，判断版本号
                        msg.what=isNewVersion(parsedJson);
                    }else {
                        //访问出错的时候
                        msg.what=ERROR;
                        msg.arg1=errorCode;
                    }

                    //结束的时间
                    endTime = System.currentTimeMillis();
                    //设置休眠的时间,保证至少休眠动画播放的3秒
                    if (endTime - startTime < 3000) {
                        SystemClock.sleep(3000 - (endTime - startTime));
                    }
                    //发送消息
                    handler.sendMessage(msg);
                    try {
                        if (reader ==null || conn == null)
                            return;
                        reader.close();
                        conn.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        }.start();

    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LOADHOME:
                    loadHome();
                    break;
                case SHOWUPDATEDIALOG:
                    showUpdateDialog();
                    break;
                case ERROR:
                    switch (msg.arg1){
                        case 404:
                            Toast.makeText(SplashActivity.this,"404资源找不到",Toast.LENGTH_SHORT).show();
                            break;
                        case 4003:
                            Toast.makeText(SplashActivity.this,"JSON格式异常",Toast.LENGTH_SHORT).show();
                            break;
                        case 4002:
                            Toast.makeText(SplashActivity.this,"URL格式异常",Toast.LENGTH_SHORT).show();
                            break;
                        case 4001:
                            Toast.makeText(SplashActivity.this,"网络连接异常",Toast.LENGTH_SHORT).show();
                            break;
                    }
                    loadHome();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 显示更新版本对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(SplashActivity.this);
        //禁用取消
//        builder.setCancelable(false);
        //取消事件
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                loadHome();
            }
        });


        builder.setTitle("更新")
               .setMessage("已经有5000人更新了版本,"+ parsedJson.getDesc())
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.e("选择更新apk","选择更新apk");
                        updateAPK();
                        installAPK();
                    }
                })
                .setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //进入主界面
                        loadHome();
                    }
                })
               .show();
    }

    /**
     * 安装下载的新版本APK
     */
    private void installAPK() {
        Intent intent =new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        Uri data= Uri.fromFile(new File("/mnt/sdcard/v2.apk"));
        String type="application/vnd.android.package-archive";
        intent.setDataAndType(data,type);
        startActivityForResult(intent,0);
        Log.e("install-安装apk","安装apk成功");
    }

    /**
     * 下载新版本
     * XUtils2
     */
    private void updateAPK() {
        HttpUtils utils=new HttpUtils();
        //先删除之前下载的
        File file=new File("/mnt/sdcard/v2.apk");
        if (file.exists()){
            file.delete();
        }

        /**
         * 参数1：URL
         * 参数2：本地路径
         * 参数3：回调
         */
        utils.download(parsedJson.getUrl(), "/mnt/sdcard/v2.apk", new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                //下载新版APK成功
                //在主线程执行
                Log.e("下载apk","下载新版APK成功");
                pb_download.setVisibility(View.INVISIBLE);
                Toast.makeText(SplashActivity.this,"下载新版APK成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                //下载过程
                pb_download.setVisibility(View.VISIBLE);
                pb_download.setMax((int)total);
                pb_download.setProgress((int)current);
                super.onLoading(total, current, isUploading);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                //下载新版APK失败
                Log.e("下载apk","下载新版apk失败");
                pb_download.setVisibility(View.INVISIBLE);
                Toast.makeText(SplashActivity.this,"下载新版APK失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 判断是否有出新版本
     * 在子线程执行的
     */
    private int isNewVersion(UrlBean parseJson) {
        int serverCode=parseJson.getVersionCode();
        if (serverCode==versionCode){
            return LOADHOME;
            //进入主界面
//            Message msg=Message.obtain();
//            msg.what=LOADHOME;
//            handler.sendMessage(msg);
        }else {
            return SHOWUPDATEDIALOG;
            //弹出更新窗口
//            Message msg=Message.obtain();
//            msg.what=SHOWUPDATEDIALOG;
//            handler.sendMessage(msg);
        }
    }

    /**
     * 解析JSON,返回Bean
     * @param jsonString
     */
    private UrlBean parseJson(StringBuilder jsonString) throws JSONException {
        UrlBean bean=new UrlBean();
        JSONObject jsonObject=new JSONObject(jsonString.toString());
        String version= (String) jsonObject.get("version");
        String url= (String) jsonObject.get("url");
        String desc= (String) jsonObject.get("desc");
        bean.setVersionCode(Integer.parseInt(version));
        bean.setUrl(url);
        bean.setDesc(desc);

        return bean;
    }

    /**
     * 跳转到主界面
     */
    private void loadHome(){
        Intent intent =new Intent(SplashActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果用户取消安装，则直接进入主界面
        loadHome();
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void checkAPI() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions=new ArrayList<>();
            int readExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeExternalStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPhoneState  = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            int readContacts  = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
            int writeContacts = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS);
            int sendSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            int readSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
            int receiveSmsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
            int location=ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int location2=ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            if(readExternalStorage != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if(writeExternalStorage != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if(readPhoneState != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if(readContacts != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_CONTACTS);
            }
            if(writeContacts != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.WRITE_CONTACTS);
            }

            if(sendSmsPermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.SEND_SMS);
            }
            if(readSmsPermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_SMS);
            }
            if(receiveSmsPermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.RECEIVE_SMS);
            }
            if(location!=PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if(location2!=PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(permissions.size()>0){
                ActivityCompat.requestPermissions(this,permissions.toArray(new String[permissions.size()]),1);
            }

            //6.0以上对应的开启SYSTEM_OVERLAY_WINDOW权限
            //这里用于来电归属地显示的自定义Toast
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
    }
}
