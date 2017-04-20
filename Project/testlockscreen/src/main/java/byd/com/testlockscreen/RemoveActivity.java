package byd.com.testlockscreen;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * 一键卸载
 */
public class RemoveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);

    }

    public void remove(View view){
        //取消激活设备管理
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        ComponentName who=new ComponentName(this,DeviceAdminSample.class);
        dpm.removeActiveAdmin(who);
        //调用卸载界面
        //卸载
        Intent remove = new Intent("android.intent.action.DELETE");
        remove.addCategory("android.intent.category.DEFAULT");
        remove.setData(Uri.parse("package:" + getPackageName()));
        startActivity(remove);//卸载用户apk的界面

    }
}
