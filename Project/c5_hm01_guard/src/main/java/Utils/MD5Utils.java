package Utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by Administrator on 2016/11/30.
 */

public class MD5Utils {
    public static String md5(String str) {
        StringBuilder mess = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
//            System.out.println("加密字节：" + Arrays.toString(md.digest(str.getBytes())));
            byte[] bytes = str.getBytes();
            byte[] digest = md.digest(bytes);
            for (byte b : digest) {
//                System.out.print("加密后的字节：" + b + ",   ");
                //把每个字节信息转换成16进制数
                int d = b & 0xff;//只取低8位
                String hexString = Integer.toHexString(Integer.valueOf(d));
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                mess.append(hexString);//每个字节对应的2位16进制数拼接
//                System.out.println("取低八位后的"+d+"的16进制："+hexString);
//                System.out.println();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        System.out.println("最终加密结果:" + mess.toString());
        return mess.toString();
    }
}
