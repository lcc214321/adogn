package com.dongyulong.dogn.tools.codec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * dogn
 *
 * @author dongyulong
 * @version v1.0
 * @date 2022/7/811:26 下午
 * @since v1.0
 */
public class EncryptUtils {

    private static final byte[] iv = {(byte) 0x16, (byte) 0x83, (byte) 0x0A,
            (byte) 0x8D, (byte) 0x27, (byte) 0x25, (byte) 0x43, (byte) 0xBE};

    // MD5的引入
    public static String MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


    /**
     * 加密
     *
     * @param encryptString
     * @return
     */
    public static String encryptDES(String encryptString) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(iv, "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

            return bytesToHexString(encryptedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 加密
     *
     * @param encryptString
     * @param encryptKey
     * @return
     */
    public static String encryptDES(String encryptString, String encryptKey) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(encryptKey.substring(
                    0, 8).getBytes());
            SecretKeySpec key = new SecretKeySpec(encryptKey.substring(0, 8)
                    .getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

            return bytesToHexString(encryptedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     *
     * @param decryptString
     * @return
     */
    public static String decryptDES(String decryptString) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(iv, "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte decryptedData[] = cipher.doFinal(hexStringToBytes(decryptString));
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     *
     * @param decryptString
     * @param decryptKey
     * @return
     * @throws Exception
     */
    public static String decryptDES(String decryptString, String decryptKey)
            throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(hexStringToBytes(decryptString));

        return new String(decryptedData);
    }

    public static String bytesToHexString(byte[] bs) {
        StringBuffer sb = new StringBuffer();
        String hex = "";
        for (int i = 0; i < bs.length; i++) {
            hex = Integer.toHexString(bs[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] hexStringToBytes(String in) {
        byte[] arrB = in.getBytes();
        int iLen = arrB.length;
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    public static String encryptDESBase64(String encryptString, String encryptKey) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(encryptKey.substring(0, 8).getBytes());
            SecretKeySpec key = new SecretKeySpec(encryptKey.substring(0, 8).getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
            byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

            return Base64.encodeBase64String(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decryptDESBase64(String decryptString, String decryptKey) throws Exception {
        if (StringUtils.isBlank(decryptString)) {
            return decryptString;
        }
        // 这里不应该做 decryptString.trim() 操作，如果加密后 +在首位或者末尾，后台接受变为 " "就会在两端。会出现真数据误以为假数据去处理
        //解决 Get 或者 Delete 请求，base64 加密后出现 + 号，而后台接受转为空格的情况 。
        String subStr = " ";
        String replaceStr = "+";
        if (decryptString.contains(subStr)) {
            decryptString = decryptString.replaceAll(subStr, replaceStr);
        }
        byte[] byteMi = Base64.decodeBase64(decryptString);
        IvParameterSpec zeroIv = new IvParameterSpec(decryptKey.substring(0, 8).getBytes());
        SecretKeySpec key = new SecretKeySpec(decryptKey.substring(0, 8).getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte decryptedData[] = cipher.doFinal(byteMi);
        return new String(decryptedData);
    }
}
