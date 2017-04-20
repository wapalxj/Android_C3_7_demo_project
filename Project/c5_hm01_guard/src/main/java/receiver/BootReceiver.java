package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.vero.c5_hm01_guard.Setup4Activity;

import Utils.EncrypyTools;
import Utils.MyConstants;
import Utils.SpTools;
import service.LostFindService;

/**
 * 开机启动的广播接收者,魅族5.0，华为荣耀8 6.0真机测试失败，模拟器测试成功
 */
public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //手机启动完成，检测SIM卡是否变化
        //取出保存的SIM卡信息
        String oldsim= SpTools.getString(context, MyConstants.SIM,"");
        //获取当前手机SIM卡信息
        TelephonyManager tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber=tm.getSimSerialNumber();
        Toast.makeText(context,""+simSerialNumber,Toast.LENGTH_LONG).show();
        //判断是否变化
        if(!oldsim.toString().equals(simSerialNumber+1)){
            //sim卡变化，发送报警短信
            //取出安全号码
            String safeNum=SpTools.getString(context,MyConstants.SAFENUMBER,"");
            safeNum= EncrypyTools.decrypt(MyConstants.ENCRYSEED,safeNum);//解密安全号码
            //发送短信给安全号码
            SmsManager sm=SmsManager.getDefault();
            sm.sendTextMessage(safeNum,"","wo shi xiao tou--hm guard",null,null);
        }

        //开机自动启动防盗服务
        if(SpTools.getBoolean(context,MyConstants.LOSTFIND,false)){
            //true:开机自动启动防盗服务
            //true,开启防盗保护
            Log.e("BootReceiver", "开机启动了防盗服务");
            Intent s=new Intent(context, LostFindService.class);
            context.startService(s);
        }
    }
}
