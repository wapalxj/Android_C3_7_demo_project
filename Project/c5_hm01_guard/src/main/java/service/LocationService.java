package service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import Utils.EncrypyTools;
import Utils.MyConstants;
import Utils.SpTools;

/**
 * 定位的服务管理器
 */
public class LocationService extends Service {

    private LocationManager lm;
    private LocationListener locationListener;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        //获取定位管理器
        //定位 定位管理器
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /**
         * @param provider 定位方式1.wifi, 2.3g/4g 3.gps
         * @param minTime 定位时间间隔 过多长时间触发监听
         * @param minDistance 定位距离差 移动距离超过多少触发监听
         * @param listener 定位监听器
         */
        /**
         * 位置变化触发的方法
         *
         * @param location
         */
        locationListener = new LocationListener() {

            /**
             * 位置变化触发的方法
             *
             * @param location
             */
            @Override
            public void onLocationChanged(Location location) {
                //获取位置变化结果
                float accuracy = location.getAccuracy();//精确度,以米为单位
                double altitude = location.getAltitude();//获取海拔高度
                double latitude = location.getLatitude();//获取纬度
                double longitude = location.getLongitude();//获取经度
                float speed = location.getSpeed();//获取移动速度
                StringBuilder tv_msg = new StringBuilder();
                tv_msg.append("精确度：" + accuracy + "\n");
                tv_msg.append("海拔高度：" + altitude + "\n");
                tv_msg.append("纬度：" + latitude + "\n");
                tv_msg.append("经度：" + longitude + "\n");
                tv_msg.append("移动速度：" + speed + "\n");
                Log.e("onLocationChanged", "" + tv_msg.toString());
                //发送短信
                //取出安全号码
                String safeNum = SpTools.getString(LocationService.this, MyConstants.SAFENUMBER, "");
                safeNum= EncrypyTools.decrypt(MyConstants.ENCRYSEED,safeNum);//解密安全号码
                //发送短信给安全号码
                Log.e("onLocationChanged", "即将发送的安全号号码：" + safeNum);
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(safeNum, "", "msg:" + tv_msg, null, null);
                //关闭GPS
                stopSelf();//关闭自己
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.e("onStatusChanged", "onStatusChanged" + status);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.e("onProviderEnabled", "onProviderEnabled" + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.e("onProviderDisabled", "onProviderDisabled" + provider);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //获取所有 提供的定位方式
        List<String> allProviders=lm.getAllProviders();
        for (String string: allProviders) {
            Log.e("allProviders定位方式:",""+string);
        }
        Criteria criteria=new Criteria();
        criteria.setCostAllowed(true);//产生费用
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//精准的
        //动态获取手机最佳定位方式
        String best=lm.getBestProvider(criteria,true);
        //注册监听
        lm.requestLocationUpdates(best, 0, 0, locationListener);

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        lm.removeUpdates(locationListener);
        lm=null;
    }
}
