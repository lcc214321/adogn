package com.dongyulong.dogn.autoconfigure.merchant.codec;

import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.common.exception.SystemException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 加密验证方式
 *
 * @author zhangshaolong
 * @create 2022/1/17
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RSAUtil {

    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";

    private static final String RSA = "RSA";

    private static final String CHARSET = "UTF-8";


    public static String sign(String rsaContent, String key) throws Exception {
        return sign(rsaContent, key, CHARSET, SIGN_ALGORITHMS);
    }

    public static String sign(String rsaContent, String key, String signType) throws Exception {
        return sign(rsaContent, key, CHARSET, SIGN_ALGORITHMS);
    }


    /**
     * base64 位decode encode
     *
     * @param rsaContent
     * @param key
     * @param charset
     * @return
     */
    public static String sign(String rsaContent, String key, String charset, String signType) throws Exception {
        if (StringUtils.isEmpty(rsaContent) || StringUtils.isEmpty(key) || StringUtils.isEmpty(charset)) {
            return null;
        }
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                Base64.getDecoder().decode(key));
        KeyFactory keyf = KeyFactory.getInstance(RSA);
        PrivateKey priKey = keyf.generatePrivate(priPKCS8);
        Signature signature = Signature.getInstance(signType);
        signature.initSign(priKey);
        signature.update(rsaContent.getBytes(charset));
        byte[] signed = signature.sign();
        return Base64.getEncoder().encodeToString(signed);

    }

    public static boolean check(String content, String sign, String publicKey) throws Exception {
        return check(content, sign, publicKey, CHARSET);
    }

    public static boolean check(String content, String sign, String publicKey, String charset) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        byte[] encodedKey = Base64.getDecoder().decode(publicKey);
        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initVerify(pubKey);
        signature.update(content.getBytes(charset));
        return signature.verify(Base64.getDecoder().decode(sign));
    }

    /**
     * 得到私钥
     *
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception -
     */
    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(org.apache.commons.codec.binary.Base64.decodeBase64(privateKey));
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
    }

    /**
     * 私钥解密
     *
     * @param data       -
     * @param privateKey -
     * @return -
     */
    public static String privateDecrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, org.apache.commons.codec.binary.Base64.decodeBase64(data), privateKey.getModulus().bitLength()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SERVICE_ERROR.getCode(), "解密字符串[" + data + "]时遇到异常");
        }
    }

    /**
     * rsa切割解码  , ENCRYPT_MODE,加密数据   ,DECRYPT_MODE,解密数据
     *
     * @param cipher  -
     * @param opmode  -
     * @param datas   -
     * @param keySize -
     * @return -
     */
    public static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        //最大块
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    //可以调用以下的doFinal（）方法完成加密或解密数据：
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SERVICE_ERROR.getCode(), "加解密阀值为[" + maxBlock + "]的数据时发生异常");
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }

}
