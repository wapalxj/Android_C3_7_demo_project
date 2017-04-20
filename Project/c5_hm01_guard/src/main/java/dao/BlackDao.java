package dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import db.BlackDB;
import domain.BlackBean;
import domain.BlackTable;

/**
 * Created by Administrator on 2017/4/6.
 * 黑名单数据业务封装类
 */

public class BlackDao {
    private BlackDB blackDB;

    public BlackDao(Context context) {
        this.blackDB = new BlackDB(context);
    }

    /**
     * 获取拦截模式
     * @param phone--电话号码
     * @return 拦截模式：1短信 2电话 3全部 4不拦截
     */
    public int getMode(String phone){
        SQLiteDatabase db=blackDB.getReadableDatabase();
        Cursor cursor=db.rawQuery("select "+BlackTable.MODE+" from "+BlackTable.BLACKTABLE +
                " where "+BlackTable.PHONE +" =? ",new String[]{phone});
        int mode=0;
        if(cursor.moveToNext()){
            //是黑名单号码
            //取出拦截模式
            mode=cursor.getInt(0);
        }else {
            mode=0;
        }
        cursor.close();
        db.close();
        return mode;
    }
    /**
     * 取出总数据条数
     * @return
     */
    public int getTotalRows(){
        SQLiteDatabase db=blackDB.getReadableDatabase();
        Cursor cursor=db.rawQuery("select count(1) from "+BlackTable.BLACKTABLE,null);
        cursor.moveToNext();
        int totalRows=cursor.getInt(0);
        cursor.close();
        db.close();
        return totalRows;
    }

    /**
     * 取出总页数
     * @param perPage---指定每页显示的数据条数
     * @return
     */
    public int getTotalPages(int perPage){
        int totalRows=getTotalRows();//总共的记录条数
        int totalPages= (int) Math.ceil(totalRows*1.0/perPage);
        return totalPages;
    }

    /**
     * 返回当前页的数据
     * @param currentPage--当前页的页码
     *  @param perPage--每页显示多少条数据
     * @return
     */
    public List<BlackBean> getPageDatas(int currentPage,int perPage){
        List<BlackBean> datas=new ArrayList<>();
        SQLiteDatabase db=blackDB.getReadableDatabase();
        //获取所有数据的数据油标
        Cursor cursor=db.rawQuery(
                "select "+BlackTable.PHONE+","
                        +BlackTable.MODE+" from "
                        +BlackTable.BLACKTABLE+" limit ? , ?",
                new String[]{((currentPage-1)*perPage)+"",""+perPage});
        while (cursor.moveToNext()){
            BlackBean blackBean=new BlackBean();
            blackBean.setPhone(cursor.getString(0));
            blackBean.setMode(cursor.getInt(1));
            datas.add(blackBean);
        }
        cursor.close();
        db.close();
        return datas;
    }
    /**
     * 返回所有的黑名单数据
     * @return
     */
    public List<BlackBean> getAllDatas() {
        List<BlackBean> datas=new ArrayList<>();
        SQLiteDatabase db = blackDB.getReadableDatabase();
        //获取所有数据的数据油标
        Cursor cursor = db.rawQuery(
                "select " + BlackTable.PHONE + ","
                        + BlackTable.MODE + " from "
                        + BlackTable.BLACKTABLE, null);
        while (cursor.moveToNext()) {
            BlackBean blackBean=new BlackBean();
            blackBean.setPhone(cursor.getString(0));
            blackBean.setMode(cursor.getInt(1));
            datas.add(blackBean);
        }
        cursor.close();
        db.close();
        return datas;
    }

    /**
     * 删除号码
     * @param phone
     */
    public void delete(String phone) {
        SQLiteDatabase db = blackDB.getWritableDatabase();
        db.delete(BlackTable.BLACKTABLE,
                BlackTable.PHONE + "=?",
                new String[]{phone});
        db.close();
    }

    /**
     * 更新拦截模式
     * @param phone：号码
     * @param mode：新模式
     */
    public void update(String phone,int mode) {
        SQLiteDatabase db=blackDB.getWritableDatabase();

        ContentValues values=new ContentValues();
        //设置新的拦截模式
        values.put(BlackTable.MODE,mode);
        //根据号码更新新的模式
        db.update(BlackTable.BLACKTABLE,
                values,
                BlackTable.PHONE+"=?",
                new String[]{phone});
        db.close();
    }

    /**
     *
     * @param phone:黑名单号码
     * @param mode：拦截模式
     */
    public void add(String phone,int mode){
        delete(phone);//如果数据已经存在，则先删除数据
        SQLiteDatabase db=blackDB.getWritableDatabase();
        ContentValues values=new ContentValues();
        //设置黑名单号码
        values.put(BlackTable.PHONE,phone);
        //设置黑名单拦截模式
        values.put(BlackTable.MODE,mode);
        //插入记录
        db.insert(BlackTable.BLACKTABLE,null,values);
        db.close();
    }

    /**
     *
     * @param blackBean:黑名单信息的封装bean
     */
    public void add(BlackBean blackBean){
        add(blackBean.getPhone(),blackBean.getMode());
    }

    /**
     * 分批加载数据
     * @param num--->取多少条数据
     * @param startIndex--->取数据其实位置
     * @return
     */
    public List<BlackBean> getMoreDatas(int num,int startIndex) {
        List<BlackBean> datas=new ArrayList<>();
        SQLiteDatabase db=blackDB.getReadableDatabase();
        //获取所有数据的数据油标
        Cursor cursor=db.rawQuery(
                "select "+BlackTable.PHONE+","
                        +BlackTable.MODE+" from "
                        +BlackTable.BLACKTABLE+" order by _id desc limit ? , ?",
                new String[]{startIndex+"",""+num});
        while (cursor.moveToNext()){
            BlackBean blackBean=new BlackBean();
            blackBean.setPhone(cursor.getString(0));
            blackBean.setMode(cursor.getInt(1));
            datas.add(blackBean);
        }
        cursor.close();
        db.close();
        return datas;
    }

}
