package byd.com.testlockscreen;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * 一键锁屏，一键卸载
 */
public class MainActivity extends AppCompatActivity {

    private DevicePolicyManager dpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设备管理员权限
        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
    }

    /**
     * 一键锁屏
     * @param view
     */
    public void lockScreen(View view){
        //如果没有激活，则提醒用户操作
        ComponentName who=new ComponentName(this,DeviceAdminSample.class);
        if(dpm.isAdminActive(who)){
            //已经有权限了

            //一键锁屏
            dpm.lockNow();
        }else {
            //帮助用户打开激活设备管理器界面，让用户激活
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "设备管理器");
            startActivityForResult(intent, 1);
        }


    }
}
