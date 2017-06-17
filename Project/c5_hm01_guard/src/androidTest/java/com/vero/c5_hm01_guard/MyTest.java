package com.vero.c5_hm01_guard;

import android.os.SystemClock;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.List;

import Utils.ServiceUtils;
import dao.BlackDao;
import db.BlackDB;
import domain.BlackBean;
import domain.BlackTable;
import engine.PhoneLocationEngine;
import engine.ReadContactsEngine;

/**
 * Created by Administrator on 2017/1/2.
 */

public class MyTest extends AndroidTestCase {
    //读取联系人
    public void testReadContacts(){
        ReadContactsEngine.readContacts(getContext());//获取的是虚拟的上下文
    }
    //指定的服务是否在运行
    public void testRunningServices(){
        ServiceUtils.isServiceRunning(getContext(),"");
    }
    //添加黑名单号码
    public void testAddBlackNumber(){
        BlackDao dao=new BlackDao(getContext());
        for (int i =0;i<20; i++) {
            dao.add("1234567"+i, BlackTable.SMS);
        }
        
    }
    //查询所有黑名单数据
    public void testQueryAllBalckDatas(){
        BlackDao dao=new BlackDao(getContext());
        List<BlackBean> datas=dao.getAllDatas();
        System.out.println(datas);
    }
    //更新某个黑名单记录的拦截模式
    public void testUpdate(){
        BlackDao dao=new BlackDao(getContext());
        dao.update("12345671",BlackTable.ALL);
    }
    //删除黑名单记录
    public void testDelete(){
        BlackDao dao=new BlackDao(getContext());
        dao.delete("12345670");
    }
    //号码归属地查询
    public void testLocation(){
        Log.e("lllll", PhoneLocationEngine.moblieQuery("13985390252",getContext()));
    }
}
