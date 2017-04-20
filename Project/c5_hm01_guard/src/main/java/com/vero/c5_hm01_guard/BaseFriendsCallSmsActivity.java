package com.vero.c5_hm01_guard;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Utils.MyConstants;
import domain.ContactBean;
import engine.ReadContactsEngine;


/**
 * 显示所有联系人界面
 */
public abstract class BaseFriendsCallSmsActivity extends ListActivity {
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    ListView lv_datas;
    ProgressDialog pd;
    List<ContactBean> datas=new ArrayList<>();
    MyAdapater myAdapater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lv_datas=getListView();
        myAdapater=new MyAdapater();
        lv_datas.setAdapter(myAdapater);
        //填充数据
        initData();
        //初始化事件
        initEvent();
    }

    /**
     * 初始化条目点击事件
     */
    private void initEvent() {
        lv_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //处理条目点击事件
                //获取数据(号码)
                ContactBean contactBean=datas.get(position);
                String phone=contactBean.getPhone();
                Intent datas=new Intent();
                datas.putExtra(MyConstants.SAFENUMBER,phone);//保存安全号码
                //设置数据
                setResult(1,datas);
                finish();//关闭自己
            }
        });
    }

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //更新界面
            switch (msg.what){
                case LOADING://正在加载数据
                    //加载对话框
                    pd=new ProgressDialog(BaseFriendsCallSmsActivity.this);
                    pd.setTitle("注意");
                    pd.setMessage("正在玩命加载数据。。。。");
                    pd.show();
                    break;
                case FINISH://数据加载完成
                    if(pd!=null){
                        pd.dismiss();
                        pd=null;//垃圾回收释放内存
                    }

                    myAdapater.notifyDataSetChanged();
                    break;
                 default:
                    break;
            }
        }
    };

    private class MyAdapater extends BaseAdapter{

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=View.inflate(getApplicationContext(),R.layout.item_friends_listview,null);
            TextView tv_name= (TextView) view.findViewById(R.id.tv_name);
            TextView tv_phone= (TextView) view.findViewById(R.id.tv_phone);
            ContactBean bean=datas.get(position);
            tv_name.setText(bean.getName());
            tv_phone.setText(bean.getPhone());

            return view;
        }
    }

    private void initData() {
        //获取数据的2中情况
        //1,本地数据
        //2,网络数据
        //子线程访问数据
        new Thread(){
            @Override
            public void run() {
                //显示获取数据的进度
                Message msg=Message.obtain();
                msg.what=LOADING;
                mHandler.sendMessage(msg);


                SystemClock.sleep(2000);//展示进度条的休眠
                //获取数据，核心代码
                datas= getDatas();

                //数据获取完成
                msg=Message.obtain();
                msg.what=FINISH;
                mHandler.sendMessage(msg);

            }
        }.start();
    }

    /**
     * 获取数据,核心代码
     * @return
     */
    public abstract List<ContactBean> getDatas();
}
