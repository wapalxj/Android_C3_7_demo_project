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
 * 读取手机联系人
 */

public class ReadContactsEngine {

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
