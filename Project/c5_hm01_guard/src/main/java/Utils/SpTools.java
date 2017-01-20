package Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/11/30.
 */

public class SpTools {
    public static void putString(Context context,String key,String value){
        SharedPreferences sp=context.getSharedPreferences(MyConstants.SPFILE,Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }
    public static String getString(Context context,String key,String defValue){
        SharedPreferences sp=context.getSharedPreferences(MyConstants.SPFILE,Context.MODE_PRIVATE);
        return sp.getString(key,defValue);
    }

    public static void putBoolean(Context context,String key,boolean value){
        SharedPreferences sp=context.getSharedPreferences(MyConstants.SPFILE,Context.MODE_PRIVATE);
        sp.edit().putBoolean(key,value).commit();
    }
    public static Boolean getBoolean(Context context,String key,boolean defValue){
        SharedPreferences sp=context.getSharedPreferences(MyConstants.SPFILE,Context.MODE_PRIVATE);
        return sp.getBoolean(key,defValue);
    }
}
