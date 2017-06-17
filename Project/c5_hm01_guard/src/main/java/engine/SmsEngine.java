package engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by wapal on 2017/5/17.
 */

public class SmsEngine {
    public static void smsBaike(Context context){
        //获取短信
        Uri uri=Uri.parse("content://sms");
        Cursor cursor=context.getContentResolver().query(uri,new String[]{"address","date","body,type"},null,null,"_id desc");
        //写到文件
        File file=new File(Environment.getExternalStorageDirectory(),"sms.xml");
        try {
            FileOutputStream fos=new FileOutputStream(file);
            PrintWriter out=new PrintWriter(fos);
            //xml文件根标记开始
            out.println("<smses count='"+cursor.getCount()+"'>");
            while (cursor.moveToNext()){
                //取短信
                //XML短信根标记开始
                out.println("<sms>");
                //属性封装
                out.println("<address>"+cursor.getString(0)+"</address>");
                out.println("<date>"+cursor.getString(1)+"</date>");
                out.println("<body>"+cursor.getString(2)+"</body>");
                out.println("<type>"+cursor.getString(3)+"</type>");
                //XML短信根结束标记
                out.println("</sms>");
                //封装成XML
                Log.e("smsBaike","smsBaike"+cursor.getString(1));
            }
            //xml文件根标记结束
            out.println("</smses>");
            out.flush();
            out.close();
            cursor.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
