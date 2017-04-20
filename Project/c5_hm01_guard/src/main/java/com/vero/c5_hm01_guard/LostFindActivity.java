package com.vero.c5_hm01_guard;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import Utils.MyConstants;
import Utils.SpTools;

public class LostFindActivity extends AppCompatActivity {
    private AlertDialog mDialog;
    private LinearLayout mLl_bottom_menu;
    private boolean menuIsShowing;
    private View mPopContentView;
    private PopupWindow mPopupWindow;
    private ScaleAnimation scaleAnim;//mPopupWindow的动画
    RelativeLayout rl_lost_find_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        //如果第一次进入该界面，则要先进入向导界面
        if(SpTools.getBoolean(this, MyConstants.ISSETUP,false)){
            //进入过向导界面，直接显示本界面
            initView();
            initPopupView();//初始化PopupWindow的view
            initPopupWindow();//初始化PopupWindow
        }else {
            //进入设置向导
            Intent i=new Intent(this,Setup1Activity.class);
            startActivity(i);
            finish();//关闭自己
        }

    }

    private void initView() {
        setContentView(R.layout.activity_lost_find);
        mLl_bottom_menu = (LinearLayout) findViewById(R.id.ll_lost_find_menu_bottom);
        rl_lost_find_root= (RelativeLayout) findViewById(R.id.rl_lost_find_root);
    }


    /**
     * 初始化PopupWindow的contentview
     */
    private void initPopupView(){
        mPopContentView = View.inflate(getApplicationContext(), R.layout.dialog_modify_name,null);

        //处理界面事件
        final EditText et_name= (EditText) mPopContentView.findViewById(R.id.et_dialog_lostfind_modify_name);
        Button btn_modify= (Button) mPopContentView.findViewById(R.id.btn_lostfind_modify);
        Button btn_modify_cancel= (Button) mPopContentView.findViewById(R.id.btn_lostfind_modify_cancel);
        btn_modify_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPopupWindow!=null && mPopupWindow.isShowing()){
                    mPopupWindow.dismiss();
                }
            }
        });
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取修改的名字
                String name=et_name.getText().toString().trim();
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getApplicationContext(),"名字不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                //保存新名字到SP
                SpTools.putString(getApplicationContext(),MyConstants.LOSTFINDNAME, name);
                mPopupWindow.dismiss();
                Toast.makeText(getApplicationContext(),"名字修改成功",Toast.LENGTH_SHORT).show();

            }
        });
    }
    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        //mPopContentView:弹出的内容
        mPopupWindow = new PopupWindow(mPopContentView,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        //获取焦点，不设置的话edittext则不能输入
        //并且此处设置后，点击menu键弹出窗口，再次点击menu键窗口则不会消失
        //不设置此处则再次点击menu键窗口会消失
        mPopupWindow.setFocusable(true);
        //动画
        scaleAnim = new ScaleAnimation(
                1,1,
                0,1,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0f
        );
        scaleAnim.setDuration(1000);
    }
    /**
     * //重新进入设置向导
     * @param view
     */
    public void enterSetup(View view){
        Intent i=new Intent(this,Setup1Activity.class);
        startActivity(i);
        finish();//关闭自己
    }

    /**
     * 菜单，使用Studio模拟器可以Ctrl+m调出菜单
     * 最后菜单不弹出了，改为在onkeyDown中弹出popupWindow
     * 所以下面2个方法和showModifyNameDialog()都已经没有用了
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_modify_name:
                Toast.makeText(getApplicationContext(),"修改菜单名",Toast.LENGTH_SHORT).show();
                //弹出对话框，让用户输入新的手机防盗名
                showModifyNameDialog();
                break;
            case R.id.menu_test:
                Toast.makeText(getApplicationContext(),"测试菜单",Toast.LENGTH_SHORT).show();
                break;
             default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 修改手机防盗名的对话框
     */
    private void showModifyNameDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);

        mDialog=builder.create();
        mDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode==KeyEvent.KEYCODE_MENU){
//            //处理Menu点击事件
//            if(!menuIsShowing){
//                mLl_bottom_menu.setVisibility(View.VISIBLE);
//            }else {
//                mLl_bottom_menu.setVisibility(View.GONE);
//            }
//            menuIsShowing=!menuIsShowing;
//        }
        if(keyCode==KeyEvent.KEYCODE_MENU){
            if(mPopupWindow!=null && mPopupWindow.isShowing()){
                mPopupWindow.dismiss();
            }else {
                int[] location=new int[2];

                //设置一个透明的背景
                mPopupWindow.setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));
                //动画
                mPopContentView.startAnimation(scaleAnim);
                //显示位置
                Log.e("popupWindow点击","x:"+location[0]+",y:"+location[1]);
                /**
                 * @param parent 父组件
                 * @param gravity 对齐方式
                 * @param x the popup's x location offset  --x坐标
                 * @param y the popup's y location offset  --y坐标
                 */
                int height =getWindowManager().getDefaultDisplay().getHeight();
                int width=getWindowManager().getDefaultDisplay().getWidth();
                mPopupWindow.showAtLocation(rl_lost_find_root, Gravity.LEFT |Gravity.TOP,width/4,height/4);

            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
