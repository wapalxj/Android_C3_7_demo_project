package engine;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Administrator on 2017/4/18.
 * 归属地查询业务封装
 * 号码有3种类型
 *           1.手机号
 *           2.固号
 *           3.服务号 110 120 95533
 */


public class PhoneLocationEngine {

    public static String locationQuery(String phoneNumber, Context context){
        String location=phoneNumber;
        //判断是什么类型的号码

        //如果是手机号
        moblieQuery(phoneNumber,context);
        //如果是固定号
        phoneQuery(phoneNumber,context);
        //服务号
        serviceQuery(phoneNumber);
        return phoneNumber;
    }

    /**
     * @param phoneNumber-待查询的手机号码
     *
     * @param context
     * @return 归属地
     */
    public static String moblieQuery(String phoneNumber, Context context){
        String res="phoneNumber";
        String path="/data/data/com.vero.c5_hm01_guard/files/address.db";
        SQLiteDatabase db=SQLiteDatabase.openDatabase(
                path,
                null,
                SQLiteDatabase.OPEN_READONLY);
        Cursor cursor=db.rawQuery(
                "select location from data2 where id =" +
                "(select outkey from data1 where id= ? );",
                new String[]{phoneNumber});
        if(cursor.moveToNext()){
            res=cursor.getString(0);
        }

        return res;
    }

    /**
     * @param phoneNumber-待查询的固话号码
     *
     * @param context
     * @return 归属地
     */
    public static String phoneQuery(String phoneNumber, Context context){
        String res="phoneNumber";
        String path="/data/data/com.vero.c5_hm01_guard/files/address.db";
        SQLiteDatabase db=SQLiteDatabase.openDatabase(
                path,
                null,
                SQLiteDatabase.OPEN_READONLY);
        //区号--分2位和3位
        String quhao="";
        //分析数据表可知:1开头和2开头的区号是2位的,其他是3位的
        // 010
        // 0755
        if(phoneNumber.charAt(1)=='1' || phoneNumber.charAt(1)=='2'){
            //2位区号
            quhao=phoneNumber.substring(1,3);
        }else {
            //3位区号
            quhao=phoneNumber.substring(1,4);
        }

        Cursor cursor=db.rawQuery("select location from data2 where area =? ;",
                new String[]{quhao});
        if(cursor.moveToNext()){
            res=cursor.getString(0);
        }

        return res;
    }

    /**
     * @param phoneNumber-待查询的服务号码
     *                   导入的数据库没有关于服务号
     *                    最好自己建一个
     *
     * @return
     */
    public static String serviceQuery(String phoneNumber){
        String res="phoneNumber";
        if(phoneNumber.equals("110")){
            res="jc";
        }else if(phoneNumber.equals("10086")){
            res="jc";
        }else {
            //...
        }

        return res;
    }
}
