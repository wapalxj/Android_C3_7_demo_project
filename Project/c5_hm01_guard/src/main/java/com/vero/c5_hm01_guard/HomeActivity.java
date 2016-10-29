package com.vero.c5_hm01_guard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    private GridView mGridView_menus;
    private int[] icons={R.mipmap.safe,R.mipmap.callmsgsafe,R.drawable.item_gv_selector,
                        R.mipmap.taskmanager,R.mipmap.netmanager,R.mipmap.trojan,
                        R.mipmap.sysoptimize,R.mipmap.atools,R.mipmap.settings};
    private String names[]={"手机防盗","通讯卫士","软件管家",
                            "进程管理","流量统计","病毒查杀",
                             "缓存清理","高级工具","设置中心",};
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initData();
    }

    /**
     * 初始化组件数据
     */
    private void initData() {
        mAdapter = new MyAdapter();
        mGridView_menus.setAdapter(mAdapter);
    }

    private void initView() {
        mGridView_menus= (GridView) findViewById(R.id.home_gridview);

    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(HomeActivity.this,R.layout.item_home_gridview,null);
            ImageView imageView= (ImageView) view.findViewById(R.id.iv_item_home_gv_icon);
            TextView textView= (TextView) view.findViewById(R.id.tv_item_home_gv_name);

            imageView.setImageResource(icons[position]);
            textView.setText(names[position]);
            return view;
        }
    }
}
