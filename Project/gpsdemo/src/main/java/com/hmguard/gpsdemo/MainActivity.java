package com.hmguard.gpsdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 真机多半测试失败
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    TextView tv_msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        check();
        initView();

        //定位 定位管理器
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /**
         * @param provider 定位方式1.wifi, 2.3g/4g 3.gps
         * @param minTime 定位时间间隔 过多长时间触发监听
         * @param minDistance 定位距离差 移动距离超过多少触发监听
         * @param listener 定位监听器
         */
        LocationListener locationListener = new LocationListener() {

            /**
             * 位置变化触发的方法
             *
             * @param location
             */
            @Override
            public void onLocationChanged(Location location) {
                //获取位置变化结果
                float accuracy=location.getAccuracy();//精确度,以米为单位
                double altitude=location.getAltitude();//获取海拔高度
                double latitude=location.getLatitude();//获取纬度
                double longitude=location.getLongitude();//获取经度
                float speed=location.getSpeed();//获取移动速度
                tv_msg.append("精确度："+accuracy+"\n");
                tv_msg.append("海拔高度："+altitude+"\n");
                tv_msg.append("纬度："+latitude+"\n");
                tv_msg.append("经度："+longitude+"\n");
                tv_msg.append("移动速度："+speed+"\n");
                Toast.makeText(MainActivity.this,"海拔高度："+altitude,Toast.LENGTH_LONG).show();
                Log.e("onLocationChanged","time:"+location.getTime());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e("onStatusChanged","onStatusChanged"+status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.e("onProviderEnabled","onProviderEnabled"+provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.e("onProviderDisabled","onProviderDisabled"+provider);
            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.e("onCreate","onCreate:");
//        lm.requestLocationUpdates("gps", 0, 0, locationListener);


    }

    private void initView() {
        tv_msg= (TextView) findViewById(R.id.tv_msg);
    }

    private boolean check() {
        List<String> pers=new ArrayList<>();
        int location=ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int location2=ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(location!=PackageManager.PERMISSION_GRANTED){
            pers.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(location2!=PackageManager.PERMISSION_GRANTED){
            pers.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(pers.size()>0){
            ActivityCompat.requestPermissions(this,pers.toArray(new String[pers.size()]),1);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("onRequestPermissions",""+grantResults[0]);
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    List<String> deniedPermission=new ArrayList<>();
                    for (int i =0;i<grantResults.length; i++) {
                        boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        int grantResult=grantResults[i];
                        Log.e("grantResult",isTip+","+grantResult);
                    }
                }
                break;
        }
    }
}
