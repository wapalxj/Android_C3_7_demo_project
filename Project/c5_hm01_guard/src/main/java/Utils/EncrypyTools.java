package Utils;

/**
 * Created by Administrator on 2017/3/11.
 * 加密解密
 */

public class EncrypyTools {
    /**加密方法
     * 简单的使用按位异或进行加密
     * @param seed---加密的种子
     * @param string---需要加密的内容
     * @return加密后的内容
     */
    public static String encrypt(int seed,String string){
        byte[] bytes=string.getBytes();
        for (int i =0;i<bytes.length; i++) {
//            bytes[i]^=seed;//对字节加密
            bytes[i]+=1;//对字节加密,不用种子了
        }
        return new String(bytes);
    }

    /**加密方法
     * 简单的使用按位异或进行加密
     * 注意：按位异或的加密，解密方法同样是按位异或
     * @param seed---解密的种子
     * @param string---需要解密的内容
     * @return加密后的内容
     */
    public static String decrypt(int seed,String string){
        byte[] bytes=string.getBytes();
        for (int i =0;i<bytes.length; i++) {
//            bytes[i]^=seed;//对字节解密
            bytes[i]-=1;//对字节加密,不用种子了
        }
        return new String(bytes);
    }
}
