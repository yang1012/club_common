package club.eryang.common.tool;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;

/**
 * com.wjcard.utils
 *
 * @Descrition Base64加密解密工具类
 * @Author yang
 * @Date 2016/7/25 10:15
 */
public class Base64Util {

    private static String CHARSET_UTF_8 = "utf-8";
    /**
     * BASE64解密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptToBytes(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * BASE64加密
     *
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String encryptFromBytes(byte[] bytes) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(bytes);
    }
    /**
     * BASE64解密
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String decrypt(String str) throws Exception {
        byte[] b = null;
        String result = null;
        if (str != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(str);
                result = new String(b, CHARSET_UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * BASE64加密
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String encrypt(String str) throws Exception {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes(CHARSET_UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    public static void main(String args[]) throws Exception {

        System.out.println(Base64Util.encrypt("asdfasf"));
        System.out.println(Base64Util.decrypt(Base64Util.encrypt("asdfasf")));

    }
}
