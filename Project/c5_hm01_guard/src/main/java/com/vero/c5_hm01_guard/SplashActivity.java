package com.vero.c5_hm01_guard;
/**
 * Splash
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
        initView();
        initData();
        initAnimation();
        checkVersion();
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
                    URL url=new URL("http://10.0.3.2:8080/guard.json");
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
                        msg.what=isNewVersion(parsedJson);
                    }else {
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
     * 更新版本
     * XUtils2
     */
    private void updateAPK() {
        HttpUtils utils=new HttpUtils();
        //先删除之前下载的
        File file=new File("/mnt/sdcard/v2.apk");
        file.delete();
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
}