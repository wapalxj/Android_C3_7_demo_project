package com.vero.c5_hm01_guard;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dao.BlackDao;
import domain.BlackBean;
import domain.BlackTable;

/**
 * 分页加载Activity
 */
public class TelSmsSafeActivityPage extends AppCompatActivity {
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ListView lv_safenumbers;
    private Button bt_addSafeNumber;
    private TextView tv_nodata;
    private ProgressBar pb_loading;
    private BlackDao dao;
    private  int totalPages;//总页数
    private int currentPage=1;//当前页码,默认为1
    private static final int perPage=20;//每页显示20条数据
    //黑名单数据的封装容器
    private List<BlackBean> datas=new ArrayList<>();
    private EditText et_gotopage;//跳转输入EditText
    private TextView tv_totalpage;//总页数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case LOADING://正在加载数据
                    //显示记载数据进度，
                    // 隐藏listview
                    // 隐藏没有数据
                    pb_loading.setVisibility(View.VISIBLE);
                    lv_safenumbers.setVisibility(View.GONE);
                    tv_nodata.setVisibility(View.GONE);

                    break;
                case FINISH:
                    //数据加载完成
                    //判断是否有数据
                    if(datas.size()>0){
                        //有数据
                        lv_safenumbers.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);
                        tv_nodata.setVisibility(View.GONE);
                        //更新数据
                        adapter.notifyDataSetChanged();
                        //初始化总页数和当前页的值
                        tv_totalpage.setText(currentPage+"/"+totalPages);
                    }else {
                        //没有数据
                        tv_nodata.setVisibility(View.VISIBLE);
                        lv_safenumbers.setVisibility(View.GONE);
                        pb_loading.setVisibility(View.GONE);
                        tv_totalpage.setText(0+"/"+0);
                    }



                    break;
                default:
                    break;
            }
        }
    };
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //取数据之前
                handler.obtainMessage(LOADING).sendToTarget();
                //取数据
                SystemClock.sleep(2000);
                //获取当前页的数据currentPage当前页码，perPage每页条数
                datas=dao.getPageDatas(currentPage,perPage);
                Log.e("datassize","size="+datas.size());
                totalPages=dao.getTotalPages(perPage);
                //取数据完成，发消息通知取数据完成
                handler.obtainMessage(FINISH).sendToTarget();
            }
        }).start();
    }

    private void initView() {
        setContentView(R.layout.activity_tel_sms_safe);
        lv_safenumbers = (ListView) findViewById(R.id.lv_telsms_safenumbers);
        bt_addSafeNumber = (Button) findViewById(R.id.bt_telsms_addsafenumber);
        tv_nodata = (TextView) findViewById(R.id.tv_telsms_nodata);
        //加载
        pb_loading = (ProgressBar) findViewById(R.id.pb_telsms_loading);

        //跳转输入EditText
        et_gotopage = (EditText) findViewById(R.id.et_telsms_gotopage);
        //总页数
        tv_totalpage = (TextView) findViewById(R.id.tv_telsms_totalpage);

        dao = new BlackDao(this);
        //黑名单适配器
        adapter=new MyAdapter();
        lv_safenumbers.setAdapter(adapter);
    }

    /**
     * viewholder
     */
    private class ItemView{
        //号码
        TextView tv_phone;
        //拦截模式
        TextView tv_mode;
        //删除
        ImageView iv;
    }
    /**
     * adapter
     */
    private MyAdapter adapter;
    private class  MyAdapter extends BaseAdapter {

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
            ItemView itemView=null;
            if(convertView==null){
                convertView=View.inflate(getApplicationContext(),R.layout.item_telsmssafe_listview,null);
                itemView=new ItemView();
                itemView.tv_phone= (TextView) convertView.findViewById(R.id.tv_telsmssafe_listview_item_number);
                itemView.tv_mode= (TextView) convertView.findViewById(R.id.tv_telsmssafe_listview_item_mode);
                itemView.iv= (ImageView) convertView.findViewById(R.id.iv_telsmssafe_listview_item_delete);
                //设置标记给convertView
                convertView.setTag(itemView);
            }else {
                itemView= (ItemView) convertView.getTag();
            }

            //初始化数据

            //获取当前你位置的数据
            BlackBean bean=datas.get(position);
            itemView.tv_phone.setText(bean.getPhone());
            switch (bean.getMode()){
                case BlackTable.SMS:
                    itemView.tv_mode.setText("短信拦截");
                    break;
                case BlackTable.TEL:
                    itemView.tv_mode.setText("电话拦截");
                    break;
                case BlackTable.ALL:
                    itemView.tv_mode.setText("全部拦截");
                    break;
                default:
                    break;
            }
            return convertView;
        }
    }

    /**
     * 下一页
     * @param view
     */
    public void nextPage(View view){
        //如果当前为最后一页，再点击下一页，处理方法1：给用户提醒，2：回到第一页
        currentPage++;//下一页
        currentPage=currentPage % totalPages ;//用第2种方式：回到第一页
        //取当前页的数据
        initData();
    }
    /**
     * 上一页
     * @param view
     */
    public void prevPage(View view){
        //如果当前为第一页，再点击上一页，处理方法1：给用户提醒，2：回到尾页
        currentPage--;//上一页
        if(currentPage==0){
            currentPage=totalPages;//用第2种方式：回到尾页
        }
        //取当前页的数据
        initData();
    }
    /**
     * 尾页
     * @param view
     */
    public void endPage(View view){
        //设置当前页为尾页
        currentPage=totalPages;
        initData();
    }
    /**
     * 跳转
     * @param view
     */
    public void jumpPage(View view){
        String jumpPageStr=et_gotopage.getText().toString().trim();
        if(TextUtils.isEmpty(jumpPageStr)){
            Toast.makeText(this,"跳转页不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        int jumpPage=Integer.parseInt(jumpPageStr);
        if(jumpPage >=1 && jumpPage<= totalPages){
            currentPage=jumpPage;
            initData();
        }else {
            Toast.makeText(this,"请按套路出牌",Toast.LENGTH_SHORT).show();
        }
    }

}

