package view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vero.c5_hm01_guard.R;

/**
 * Created by Administrator on 2017/3/15.
 */

public class SettingCenterItemView extends LinearLayout{
    private static String namespace="http://schemas.android.com/apk/res-auto";
    private TextView tv_title;
    private TextView tv_content;
    private CheckBox cb_check;
    private String[] contents;
    private View item;//布局

    /**
     * 通过代码实例化调用该构造函数
     * @param context
     */
    public SettingCenterItemView(Context context) {
        super(context);
        initView();
    }

    /**配置文件中，反射实例化设置属性参数：
     * XML中设置属性参数调用该构造函数
     * @param context
     * @param attrs
     */
    public SettingCenterItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initEvent();
        String title=attrs.getAttributeValue(namespace,"title");
        String content=attrs.getAttributeValue(namespace,"content");
        int rId=attrs.getAttributeResourceValue(namespace,"cb_bg",-1);
        if (rId!=-1){
            cb_check.setButtonDrawable(rId);
        }
        tv_title.setText(title);
        Log.e("title","title="+title+",content"+content);
        contents = content.split("-");


        //初始设置未选中的颜色为红色
        tv_content.setText(contents[0]);
        tv_content.setTextColor(Color.RED);
    }


    /**
     * 根布局item点击事件
     * @param listener
     */
    public void setItemClickListener(OnClickListener listener){
        //通过自定义组合空间，把事件传递个子组件
        item.setOnClickListener(listener);
    }

    //设置cb_check状态
    public void setChecked(boolean isChecked){
        cb_check.setChecked(isChecked);
    }
    //获取cb_check状态
    public boolean isChecked(){
        return cb_check.isChecked();
    }
    /**
     * 初始化cb_check事件
     */
    private void initEvent() {
        item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_check.setChecked(!cb_check.isChecked());
            }
        });
        cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tv_content.setText(contents[1]);
                    tv_content.setTextColor(Color.GREEN);
                }else {
                    //设置未选中的颜色为红色
                    tv_content.setText(contents[0]);
                    tv_content.setTextColor(Color.RED);
                }
            }
        });
    }


    /**
     * 初始化子组件
     */
    private void initView(){
        //布局
        item = View.inflate(getContext(), R.layout.item_setting_center,null);
        //子控件
        //标题.内容，复选框
        tv_title = (TextView) item.findViewById(R.id.tv_setting_autoupdate_title);
        tv_content = (TextView) item.findViewById(R.id.tv_setting_autoupdate_content);
        cb_check = (CheckBox) item.findViewById(R.id.cb_setting_autoupdate_checked);
        addView(item);
    }

}
