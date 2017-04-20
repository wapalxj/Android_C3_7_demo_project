package com.vero.c5_hm01_guard;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Utils.MyConstants;
import dao.BlackDao;
import domain.BlackBean;
import domain.BlackTable;

/**
 *
 * 分批加载Activity
 */
public class TelSmsSafeActivity extends AppCompatActivity {
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ListView lv_safenumbers;
    private Button bt_addSafeNumber;
    private TextView tv_nodata;
    private ProgressBar pb_loading;
    private final int MOREDATASCOUNTS=7;//分批加载的数据个数
    //黑名单数据的封装容器
    private List<BlackBean> datas=new ArrayList<>();
    private BlackDao dao;
    private List<BlackBean> moreDatas;//动态加载数据的临时容器
    private AlertDialog dialog;
    private View popupContentView;//点击添加弹出的popupwindow的布局
    private PopupWindow mPopupWindow;//点击添加弹出的popupwindow
    ScaleAnimation scaleAnim;//popup的动画
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
        initPopupWindow();
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        //popupwindow布局
        popupContentView = View.inflate(getApplicationContext(), R.layout.popup_blacknumber_item,null);
        //手动添加
        TextView tv_shoudong= (TextView) popupContentView.findViewById(R.id.tv_popup_black_shoudong);
        //联系人导入
        TextView tv_contact= (TextView) popupContentView.findViewById(R.id.tv_popup_black_contacts);
        //电话日志
        TextView tv_phonelog= (TextView) popupContentView.findViewById(R.id.tv_popup_black_phonelog);
        //短信日志
        TextView tv_smslog= (TextView) popupContentView.findViewById(R.id.tv_popup_black_smslog);

        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tv_popup_black_shoudong:
                        //手动添加
                        System.out.println("手动添加");
                        showInputBlackNumberDialog("");
                        break;
                    case R.id.tv_popup_black_contacts:
                        //联系人导入
                        System.out.println("联系人导入");
                        Intent intent=new Intent(TelSmsSafeActivity.this,FriendsActivity.class);
                        //启动联系人界面，并获取结果
                        startActivityForResult(intent,1);
                        break;
                    case R.id.tv_popup_black_phonelog:
                        //电话日志
                        System.out.println("电话日志");
                        Intent intent2=new Intent(TelSmsSafeActivity.this,CallLogsActivity.class);
                        //启动联系人界面，并获取结果
                        startActivityForResult(intent2,1);
                        break;
                    case R.id.tv_popup_black_smslog:
                        //短信日志
                        System.out.println("短信日志");
                        Intent intent3=new Intent(TelSmsSafeActivity.this,SmsLogsActivity.class);
                        //启动联系人界面，并获取结果
                        startActivityForResult(intent3,1);
                        break;
                     default:
                        break;
                }

                //关闭
                closepopuoWindow();
            }
        };
        //添加事件
        tv_shoudong.setOnClickListener(listener);
        tv_contact.setOnClickListener(listener);
        tv_phonelog.setOnClickListener(listener);
        tv_smslog.setOnClickListener(listener);
        //popupwindow
        mPopupWindow = new PopupWindow(popupContentView, LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //显示动画
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

    //获取联系人，电话记录，短信记录的电话号码
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //获取联系人，电话记录，短信记录的电话号码
        if(data!=null){
            //用户点击条目获取结果
            String phone=data.getStringExtra(MyConstants.SAFENUMBER);
            //设置黑名单电话号码,复用showInputBlackNumberDialog
            showInputBlackNumberDialog(phone);
        }else {
            //用户点击退出
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 显示PopupWindow
     */
    private void showPopupWindow(){
        if(mPopupWindow!=null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }else {
            if(mPopupWindow!=null ){
                //获取按钮坐标
                int[] location=new int[2];
                bt_addSafeNumber.getLocationInWindow(location);
                //显示动画
                popupContentView.startAnimation(scaleAnim);
                //右上角对齐
                mPopupWindow.showAtLocation(bt_addSafeNumber,
                        //右上角对齐
                        Gravity.END|Gravity.TOP,
                        //X坐标：btn的X坐标-屏幕的宽度-btn的宽度
//                        location[0]-getWindowManager().getDefaultDisplay().getWidth()-bt_addSafeNumber.getMeasuredWidth(),
//                        location[1]+bt_addSafeNumber.getMeasuredHeight());
                        location[0]-getWindowManager().getDefaultDisplay().getWidth(),
                        location[1]+bt_addSafeNumber.getMeasuredHeight());
//                        0,
//                        location[1]+bt_addSafeNumber.getMeasuredHeight());
            }
        }
    }
    private void closepopuoWindow(){
        if(mPopupWindow!=null && mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }

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
                    if(moreDatas.size()>0){
                        //有数据
                        lv_safenumbers.setVisibility(View.VISIBLE);
                        pb_loading.setVisibility(View.GONE);
                        tv_nodata.setVisibility(View.GONE);
                        //更新数据
                        adapter.notifyDataSetChanged();
                    }else {
                        if(datas.size()>0){//分批加载的时候，数据已经全部加载完了，没有更多数据了
                            Toast.makeText(getApplicationContext(),"没有更多数据",Toast.LENGTH_SHORT).show();
                            lv_safenumbers.setVisibility(View.VISIBLE);
                            pb_loading.setVisibility(View.GONE);
                            tv_nodata.setVisibility(View.GONE);
                            return;
                        }
                        //没有数据
                        tv_nodata.setVisibility(View.VISIBLE);
                        lv_safenumbers.setVisibility(View.GONE);
                        pb_loading.setVisibility(View.GONE);
                    }
                    break;
                 default:
                    break;
            }
        }
    };

    private void initEvent() {
        //给ListView设置滚动事件
        lv_safenumbers.setOnScrollListener(new AbsListView.OnScrollListener() {
            /**
             * 状态改变调用此方法
             * scrollState:3种：
             * OnScrollListener.SCROLL_STATE_FLING-->惯性滑动
             * AbsListView.OnScrollListener.SCROLL_STATE_IDLE-->不滑动
             * AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL-->按住滑动
             * @param view
             * @param scrollState
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //监控静止时
                //判断是否显示最后一条数据，如果是，那就加载更多数据
                if(scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    //获取最后显示的数据的位置
                    int lastVisiblePosition =lv_safenumbers.getLastVisiblePosition();
                    if(lastVisiblePosition ==datas.size()-1){
                        //最后显示的数据的位置 == 最后一条数据
                        //加载更多
                        initData();
                    }
                }

            }

            /**
             * 按住滑动触发
             * @param view
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }
    private void initData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //取数据之前
                handler.obtainMessage(LOADING).sendToTarget();
                //取数据
                SystemClock.sleep(1000);

                //获取分批加载的数据
                moreDatas = dao.getMoreDatas(MOREDATASCOUNTS,datas.size());
                //把分批获取的数据全部加进来
                datas.addAll(moreDatas);
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
        pb_loading = (ProgressBar) findViewById(R.id.pb_telsms_loading);

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
    private class  MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            int size=datas.size();
            if(size==0){
                //如果为空
                tv_nodata.setVisibility(View.VISIBLE);
                lv_safenumbers.setVisibility(View.GONE);
                pb_loading.setVisibility(View.GONE);
            }
            return size;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
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
            final BlackBean bean=datas.get(position);
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
            //设置删除数据事件
            itemView.iv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(TelSmsSafeActivity.this);
                    builder.setTitle("注意");
                    builder.setMessage("是否真的删除该数据？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //从数据库中删除当前数据
                            dao.delete(bean.getPhone());//取出当前行数据里的黑名单号码
                            //删除容器中的数据
                            datas.remove(position);
                            if(datas.size()<9 || position==datas.size()){
                                //条件1.当前页数据少于10条，则继续加载进来
                                //条件2.当删除的是当前页面最后一条时,继续加载进来
                                initData();
                            }else {
                                //刷新界面
                                adapter.notifyDataSetChanged();
                            }
//                            if(datas.isEmpty()){
//                                //当前页被删除完了,数据库中也许还有数据
                                  //当数据库中还存在数据时，这种做法用户体验不好
//                                initData();
//                            }

                        }
                    });
                    builder.setNegativeButton("点错了", null);

                    builder.show();
                }
            });
            return convertView;
        }
    }

    public void addBlackNumber(View v){
//        showInputBlackNumberDialog();
            showPopupWindow();
    }
    /**
     * 显示添加黑名单对话框:手动导入
     */
    private void showInputBlackNumberDialog(String phone) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        View view=View.inflate(getApplicationContext(), R.layout.dialog_addblacknumber,null);
        builder.setView(view);
        //黑名单号码ET
        final EditText et_blackNumber= (EditText) view.findViewById(R.id.et_telsmssafe_blacknumber);
        //设置初始的黑名单号码
        et_blackNumber.setText(phone);

        //短信拦截cb
        final CheckBox cb_sms= (CheckBox) view.findViewById(R.id.cb_telsmssafe_smsmode);
        //电话拦截cb
        final CheckBox cb_phone= (CheckBox) view.findViewById(R.id.cb_telsmssafe_phonemode);
        //按钮：添加黑名单
        Button btn_add= (Button) view.findViewById(R.id.btn_telsmssafe_add);
        //按钮：取消添加黑名单
        Button btn_cancel= (Button) view.findViewById(R.id.btn_telsmssafe_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加黑名单
                String phone=et_blackNumber.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    Toast.makeText(TelSmsSafeActivity.this,"号码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!cb_phone.isChecked() && !cb_sms.isChecked()){
                    Toast.makeText(TelSmsSafeActivity.this,"至少选择一种拦截模式",Toast.LENGTH_SHORT).show();
                    return;
                }

                int mode=0;
                if(cb_phone.isChecked()){
                    mode |= BlackTable.TEL;//设置电话的拦截模式
                }
                if(cb_sms.isChecked()){
                    mode |= BlackTable.SMS;//设置短信的拦截模式
                }

                //添加和刷新数据
                BlackBean blackBean=new BlackBean();
                blackBean.setPhone(phone);
                blackBean.setMode(mode);
                dao.add(blackBean);//添加数据到黑名单表
                //出现相同号码时避免重复先删除，该删除方法主要依据hashcode()和equals()共同决定数据是否一致
                datas.remove(blackBean);
                datas.add(0,blackBean);//添加数据到容器
                //让listview显示第一条数据
                lv_safenumbers.setSelection(0);
                adapter.notifyDataSetChanged();//刷新界面
                handler.obtainMessage(FINISH).sendToTarget();//显示
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }
}
