package Utils;

import android.app.*;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 */

public class ServiceUtils {
    /**
     *
     * @param context
     * @param serviceName:service的完整的报名
     * @return service是否在运行
     */
    public static boolean isServiceRunning(Context context,String serviceName){
        boolean isRunning=false;
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取android手机中运行的所有的服务
        List<ActivityManager.RunningServiceInfo> infos=am.getRunningServices(50);
        for (ActivityManager.RunningServiceInfo rSinfo: infos ) {
//            Log.e("serices","serices: "+info.service.getClassName());
            if(rSinfo.service.getClassName().equals(serviceName)){
                //名字一直，表示该服务在运行中
                isRunning=true;
            }
        }
        return  isRunning;
    }
}
