package com.a03_14_popupwindow;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private PopupWindow mPopupWindow;
    private AlphaAnimation alphaAnim;
    private ScaleAnimation scaleAnim;
    private View mPopContentView;
    private LinkedList<String> datas;
    private ArrayAdapter arrayAdapter;
    private EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et= (EditText) findViewById(R.id.pop);
        datas=new LinkedList<>();
        initPopup();
    }

    public void addData(String s){
        datas.addFirst(s);

    }
    public void add(View view){
        addData(et.getText().toString().trim());
    }
    private void initPopup() {
        //mPopContentView:弹出的内容
        mPopContentView = View.inflate(getApplicationContext(), R.layout.popup,null);
        ListView listView= (ListView) mPopContentView.findViewById(R.id.list);
        arrayAdapter=new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,datas);
        listView.setAdapter(arrayAdapter);
        mPopupWindow = new PopupWindow(mPopContentView,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setOutsideTouchable(true);
//        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //设置一个透明的背景
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //动画
        alphaAnim = new AlphaAnimation(0,1);
        alphaAnim.setDuration(3000);

        scaleAnim = new ScaleAnimation(
                1,1,
                0,1,
                Animation.RELATIVE_TO_SELF,0.5f,
                Animation.RELATIVE_TO_SELF,0f
                );
        scaleAnim.setDuration(300);
    }

    public void popupWindow(View view){
        if(mPopupWindow!=null && mPopupWindow.isShowing()){
            Log.e("popupWindow点击","dismiss");
            mPopupWindow.dismiss();
        }else {
            int[] location=new int[2];

            //动画
            mPopContentView.startAnimation(scaleAnim);
            //显示位置
            //获取父控件的位置
            view.getLocationInWindow(location);
            Log.e("popupWindow点击","--x:"+location[0]+",y:"+location[1]);
            /**
             * @param parent 父组件
             * @param gravity 对齐方式
             * @param x the popup's x location offset  --x坐标
             * @param y the popup's y location offset  --y坐标
             */
            mPopupWindow.showAtLocation(view, Gravity.LEFT |Gravity.TOP,location[0]+view.getWidth(),location[1]+view.getHeight());

        }
    }

    @Override
    protected void onDestroy() {
        if (mPopupWindow!=null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
            mPopupWindow=null;
        }
        super.onDestroy();
    }
}
