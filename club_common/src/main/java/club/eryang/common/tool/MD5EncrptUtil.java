package club.eryang.common.tool;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * com.wjcard.utils
 *
 * @Descrition MD5加密工具类
 * @Author yang
 * @Date 2016/7/29 11:18
 */
public class MD5EncrptUtil {

    private static final String KEY_MD5 = "MD5";

    /**
     * MD5加密
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String encrypt(String str) throws Exception {

        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
        md5.update(str.getBytes());
        byte b[] = md5.digest();
        int i;
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");
            buf.append(Integer.toHexString(i));
        }
        //32位加密
        return buf.toString();
        // 16位的加密
        //return buf.toString().substring(8, 24);
    }

    public static void main(String args[]) throws Exception {

        System.out.println(MD5EncrptUtil.encrypt("123456"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(new Date()).toString());

    }
}
