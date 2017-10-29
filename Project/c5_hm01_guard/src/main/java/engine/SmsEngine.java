package engine;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;

import Utils.EncrypyTools;
import Utils.MyConstants;
import domain.SmsBean;

/**
 * Created by wapal on 2017/5/17.
 */

public class SmsEngine {
    public interface BaikeProgress{
        void show();
        void setMax(int max);
        void setProgress(int progress);
        void end();
    }

    //内容中有json特殊字符的转换
    public static String changeStr(String json){
        json.replaceAll(",","，");
        json.replaceAll(":","：");
        json.replaceAll("\\[","【");
        json.replaceAll("\\]","】");
        json.replaceAll("\\{","<");
        json.replaceAll("\\}",">");
        json.replaceAll("\"","“");
        return json.toString();
    }
    //短信的还原json
    public static void smsResumeJson(final Activity context, final BaikeProgress pd){
        new Thread(){
            @Override
            public void run() {
                //通过内容提供者保存短信
                Uri uri=Uri.parse("content://sms");
                try {
                    FileInputStream fis=new FileInputStream(new File(Environment.getExternalStorageDirectory(),"sms.json"));
                    StringBuilder jsonSmsStr=new StringBuilder();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(fis));
                    String line=reader.readLine();
                    while (line != null) {
                        jsonSmsStr.append(line);
                        line=reader.readLine();
                    }
                    Log.e("jsonSms",jsonSmsStr.toString());
                    //解析JSON
//                    JSONObject jsonObject=new JSONObject(jsonSmsStr.toString());
//                    final int count= Integer.parseInt(jsonObject.getString("count"));
                    //用gson
                    Gson gson=new Gson();
                    SmsBean smsBean=gson.fromJson(jsonSmsStr.toString(), SmsBean.class);
                    final int count =smsBean.getCount();
                    Log.e("gson",smsBean.getCount()+"");

                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.show();
                            pd.setMax(count);
                        }
                    });
                    //循环读取短信gson
                    for (int i = 0; i < count; i++) {
                        //对短信解密处理
                        SmsBean.Sms sms=smsBean.getSmses().get(i);
                        String addr=sms.getAddress();
                        String date=sms.getDate();
                        String body=sms.getBody();
                        String mbody=EncrypyTools.decrypt(1,body);
                        String type=sms.getType();
                        ContentValues values=new ContentValues();
                        values.put("address",addr);
                        values.put("date",date);
                        values.put("body",mbody);
                        values.put("type",type);
                        context.getContentResolver().insert(uri,values);
                        pd.setProgress(i+1);
                        SystemClock.sleep(300);
                        Log.e("gson",addr+"--"+mbody+"--");
                    }

                    //循环读取短信jsonobject
//                    JSONArray jsonArray= (JSONArray) jsonObject.get("smses");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject obj= (JSONObject) jsonArray.get(i);
//                        String addr=obj.getString("address");
//                        String date=obj.getString("date");
//                        String body=obj.getString("body");
//                        //对短信解密处理
//                        String mbody=EncrypyTools.decrypt(1,body);
//                        String type=obj.getString("type");
//                        ContentValues values=new ContentValues();
//                        values.put("address",addr);
//                        values.put("date",date);
//                        values.put("body",body);
//                        values.put("type",type);
//                        context.getContentResolver().insert(uri,values);
//                        pd.setProgress(i+1);
//                        SystemClock.sleep(300);
//                    }

                    reader.close();
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.end();
                        }
                    });
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
    //短信的备份，JSON方式：将BaikeProgress接口作为参数传递进来
    public static void smsBaikeJson(final Activity context, final BaikeProgress pd){
        new Thread(){
            @Override
            public void run() {
                //获取短信
                Uri uri=Uri.parse("content://sms");
                final Cursor cursor=context.getContentResolver().query(uri,new String[]{"address","date","body,type"},null,null,"_id desc");
                //写到文件
                if (cursor.getCount()==0) {
                    //没有短信
                    return;
                }
                File file=new File(Environment.getExternalStorageDirectory(),"sms.json");
                try {
                    FileOutputStream fos=new FileOutputStream(file);
                    PrintWriter out=new PrintWriter(fos);
                    Log.e("SMS 备份",""+cursor.getCount());
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.show();
                            pd.setMax(cursor.getCount());
                        }
                    });

                    int progress = 0;
                    //json
                    //{"count":"xx"}
                    out.println("{\"count\":\""+cursor.getCount()+"\"");
                    out.println(",\"smses\":[");
                    while (cursor.moveToNext()){
                        progress++;
                        //取短信

                        //XML短信根标记开始
                        if (cursor.getPosition()==0){
                            out.println("{");
                        }else {
                            out.println(",{");
                        }
                        //属性封装
                        //{"address":"xxx",
                        out.println("\"address\":\""+cursor.getString(0)+"\",");
                        //"date":"xxx",
                        out.println("\"date\":\""+cursor.getString(1)+"\",");
                        //"body":"xxx",
                        //对短信加密处理
                        String mbody=EncrypyTools.encrypt(1,changeStr(cursor.getString(2)));
                        Log.e("body",mbody);
                        out.println("\"body\":\""+mbody+"\",");
                        //"type":"xxx",}
                        out.println("\"type\":\""+cursor.getString(3)+"\"");
                        //XML短信根结束标记
                        out.println("}");
                        //封装成json
                        Log.e("smsBaike","smsBaike"+cursor.getString(1));
                        pd.setProgress(progress);
                        SystemClock.sleep(300);
                    }
                    //xml文件根标记结束
                    out.println("]}");
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.end();
                        }
                    });
                    out.flush();
                    out.close();
                    cursor.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }


    //XML方式：将BaikeProgress接口作为参数传递进来
    public static void smsBaikeXml(Activity context, final BaikeProgress pd){
        //获取短信
        Uri uri=Uri.parse("content://sms");
        final Cursor cursor=context.getContentResolver().query(uri,new String[]{"address","date","body,type"},null,null,"_id desc");
        //写到文件
        File file=new File(Environment.getExternalStorageDirectory(),"sms.xml");
        try {
            FileOutputStream fos=new FileOutputStream(file);
            PrintWriter out=new PrintWriter(fos);
            Log.e("SMS 备份",""+cursor.getCount());
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.show();
                    pd.setMax(cursor.getCount());
                }
            });

            int progress = 0;
            //xml文件根标记开始
            out.println("<smses count='"+cursor.getCount()+"'>");
            while (cursor.moveToNext()){
                progress++;
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
                pd.setProgress(progress);
                SystemClock.sleep(300);
            }
            //xml文件根标记结束
            out.println("</smses>");
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pd.end();
                }
            });
            out.flush();
            out.close();
            cursor.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}

