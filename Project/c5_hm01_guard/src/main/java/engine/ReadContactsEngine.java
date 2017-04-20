package engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import domain.ContactBean;

/**
 * Created by Administrator on 2016/12/26.
 * 读取手机联系人的功能类
 */

public class ReadContactsEngine {
    /**
     * 短信日志记录
     * @param context
     * @return
     */
    public static List<ContactBean> readSmsLog(Context context){
        //1.电话日志的数据库
        //2.通过分析，联系人数据库不能直接访问，看上层源码需要通过ContentProvider访问
        //3.uri:content://sms
        Uri uri=Uri.parse("content://sms");
        Cursor cursor=context.getContentResolver().query(uri,new String[]{"address"},null,null,"_id desc");
        List<ContactBean> datas=new ArrayList<>();
        while (cursor.moveToNext()) {
            ContactBean contactBean = new ContactBean();
            String phone = cursor.getString(0);//获取电话号码
//            String name = cursor.getString(1);//获取名字
//            contactBean.setName(name);
            contactBean.setPhone(phone);
            //添加数据
            datas.add(contactBean);
        }
        return datas;
    }
    /**
     * 电话日志记录
     * @param context
     * @return
     */
    public static List<ContactBean> readCallLog(Context context){
        //1.电话日志的数据库
        //2.通过分析，联系人数据库不能直接访问，看上层源码需要通过ContentProvider访问
        //3.uri:content://call_log/calls
        Uri uri=Uri.parse("content://call_log/calls");
        Cursor cursor=context.getContentResolver().query(uri,new String[]{"number","name"},null,null,"_id desc");
        List<ContactBean> datas=new ArrayList<>();
        while (cursor.moveToNext()) {
            ContactBean contactBean = new ContactBean();
            String phone = cursor.getString(0);//获取电话号码
            String name = cursor.getString(1);//获取名字
            contactBean.setName(name);
            contactBean.setPhone(phone);
            //添加数据
            datas.add(contactBean);
        }
        return datas;
    }

    /**
     * 联系人列表获取
     * @param context
     * @return
     */
    public static List<ContactBean> readContacts(Context context){
        List<ContactBean> datas=new ArrayList<>();
        Uri contactsUri=Uri.parse("content://com.android.contacts/contacts");
        Uri datasUri=Uri.parse("content://com.android.contacts/data");
        Cursor cursor=context.getContentResolver().query(contactsUri,new String[]{"_id"},null,null,null);
        while (cursor.moveToNext()){
            ContactBean contactBean=new ContactBean();
            String id=cursor.getString(0);
            //从另外一张表中查询数据
            Cursor cursor2=context.getContentResolver().query(datasUri,new String[]{"data1","mimetype"},"raw_contact_id=?",new String[]{id},null);
            //由于Android系统的联系人表的特殊性：
            //一次循环：一个联系人的一部分信息
            //两次循环：一个联系人的所有信息
            while (cursor2.moveToNext()){
                String data=cursor2.getString(0);
                String mimetype=cursor2.getString(1);

                if(mimetype.equals("vnd.android.cursor.item/name")){
                    Log.e("id-name","id:"+id+",name:"+data);
                    //封装
                    contactBean.setName(data);
                }else if(mimetype.equals("vnd.android.cursor.item/phone_v2")){
                    Log.e("id-name","id:"+id+",phone:"+data);
                    //封装
                    contactBean.setPhone(data);
                }
            }
            cursor2.close();
            datas.add(contactBean);
        }
        cursor.close();
        return datas;
    }
}
