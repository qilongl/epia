package com.easipass.epia.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Security;

/**
 * 加密常用工具方法类
 */
public final class EncryptHelper {
    private static final String Algorithm = "DESede"; //定义 加密算法,可用 DES,DESede,Blowfish
    private static final int KEY_LENGTH = 24;  //定义 加密算法密钥长度　des 8,desede 24

    public static String getBASE64(byte[] s) {
        Base64 base64 = new Base64();

        if (s == null) return null;
        try {
            return (new String(base64.encode(s), "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    public static byte[] encodeBASE64(byte[] s) {
        Base64 base64 = new Base64();
        try {
            return base64.encode(s);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    // 将 BASE64 编码的字符串 s 进行解码
    public static byte[] getFromBASE64(String s) {
        if (s == null) return null;
        Base64 decoder = new Base64();
        try {
            byte[] b = decoder.decode(s.getBytes("UTF-8"));
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] decodeBASE64(byte[] s) {
        if (s == null) return null;
        Base64 decoder = new Base64();
        try {
            byte[] b = decoder.decode(s);
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] genKey(String key) throws Exception {

        byte keyBytes[];
        switch (key.length()) {
            case 24:
                keyBytes = key.getBytes("UTF-8");
                break;
            case 16:
                key = key + key.substring(0, 8);
                keyBytes = key.getBytes("UTF-8");
                break;
            default:
                throw new Exception("密钥长度不对！");
        }

        return keyBytes;

    }

    private static byte[] genMsg(String msg) throws Exception {

        byte[] src = msg.getBytes("UTF-8");
        byte[] msgBytes;

        if ((src.length % 8) != 0) {
            int remain = 8 - (src.length % 8);
            int total = src.length + remain;
            msgBytes = new byte[total];
        } else {
            msgBytes = new byte[src.length];
        }
        for (int count = 0; count < src.length; count++) {
            msgBytes[count] = src[count];
        }
        return msgBytes;

    }

    private static byte[] genMsg1(byte[] src) throws Exception {

        byte[] msgBytes;

        if ((src.length % 8) != 0) {
            int remain = 8 - (src.length % 8);
            int total = src.length + remain;
            msgBytes = new byte[total];
        } else {
            msgBytes = new byte[src.length];
        }
        for (int count = 0; count < src.length; count++) {
            msgBytes[count] = src[count];
        }
        return msgBytes;

    }

    private static IvParameterSpec genIV() {

        byte[] myIV = new byte[8];
        IvParameterSpec ivspec = new IvParameterSpec(myIV);

        return ivspec;

    }

    public static byte[] encrypt(String msg, String key) throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());

        //生成密钥

        byte[] keyBytes = genKey(key);
        byte[] msgBytes = genMsg(msg);


        SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);
        IvParameterSpec ivspec = genIV();

        //加密
        Cipher c1 = Cipher.getInstance("DESede/CBC/NoPadding");
        //Cipher c1 = Cipher.getInstance("DES/CBC/NoPadding");
        c1.init(Cipher.ENCRYPT_MODE, deskey, ivspec);
        byte[] result = c1.doFinal(msgBytes);

        //System.out.println(String.valueOf(result.length));
        return (result);


    }

    public static byte[] encrypt1(byte[] msg, String key) throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());

        //生成密钥

        byte[] keyBytes = genKey(key);
        byte[] msgBytes = genMsg1(msg);


        SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);
        IvParameterSpec ivspec = genIV();

        //加密
        Cipher c1 = Cipher.getInstance("DESede/CBC/NoPadding");
        //Cipher c1 = Cipher.getInstance("DES/CBC/NoPadding");
        c1.init(Cipher.ENCRYPT_MODE, deskey, ivspec);
        byte[] result = c1.doFinal(msgBytes);

        //System.out.println(String.valueOf(result.length));
        return (result);


    }


    public static String decrypt(byte[] msg, String key) throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());

        //生成密钥
        byte[] keyBytes = genKey(key);
        SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);

        IvParameterSpec ivspec = genIV();

        byte[] src = msg;
        //解密
        Cipher c1 = Cipher.getInstance("DESede/CBC/NoPadding");
        //Cipher c1 = Cipher.getInstance("DES/CBC/NoPadding");
        c1.init(Cipher.DECRYPT_MODE, deskey, ivspec);
        return (new String(c1.doFinal(src), "UTF-8"));
        //return(String.valueOf(src.length));

    }

    public static byte[] decrypt1(byte[] msg, String key) throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());

        //生成密钥
        byte[] keyBytes = genKey(key);
        SecretKey deskey = new SecretKeySpec(keyBytes, Algorithm);

        IvParameterSpec ivspec = genIV();

        byte[] src = msg;
        //解密
        Cipher c1 = Cipher.getInstance("DESede/CBC/NoPadding");
        //Cipher c1 = Cipher.getInstance("DES/CBC/NoPadding");
        c1.init(Cipher.DECRYPT_MODE, deskey, ivspec);
        return c1.doFinal(src);
        //return(String.valueOf(src.length));

    }

    public static String encodePassword(String strIn) {
        return DigestUtils.md5Hex(strIn);
    }

    public static String encodeMd5(String strIn) {
        try {
            return DigestUtils.md5Hex(strIn.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return "";
        }
    }

    /**
     * BASE64解密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return (new BASE64Decoder()).decodeBuffer(key);
    }

    /**
     * BASE64加密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(byte[] key) throws Exception {
        return (new BASE64Encoder()).encodeBuffer(key);
    }


    /**
     * @param m    密钥
     * @param text 文本
     * @returns {string}
     */
    public static String simpleEncrypt(String m, String text, String split) {
        String[] chars = text.split(split);
        StringBuffer last = new StringBuffer();
        int j = 0;
        for (int i = 0; i < chars.length; i++) {
            if (StringHelper.isEmpty(chars[i])) continue;
            if (j == m.length()) j = 0;

            char key = m.charAt(j);
            char text2 = (char) (Integer.valueOf(chars[i]).intValue() ^ key);

            last.append(text2);
            j++;

        }
        return last.toString();
    }

    public static void main(String[] args) {
        try {
//			System.out.println(encodePassword("123456"));
//
//			String jmh = encryptBASE64("sysadmin;123456;2013-08-05 13:54:34".getBytes());
//			System.out.println("1====="+jmh);
//
//			byte[] jiemh = decryptBASE64(jmh);
//			System.out.println("2====="+new String(jiemh));
            System.out.println(simpleEncrypt("12F3", "ylbd1236", "-"));
            System.out.println(simpleEncrypt("N2PY123DMDYLZD234LDNZUDENDLFD", "4J$<BFR  -7", "-"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

