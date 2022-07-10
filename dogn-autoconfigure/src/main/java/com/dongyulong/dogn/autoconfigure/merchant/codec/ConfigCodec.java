package com.dongyulong.dogn.autoconfigure.merchant.codec;

import com.dongyulong.dogn.common.exception.ErrorCode;
import com.dongyulong.dogn.common.exception.SystemException;
import com.dongyulong.dogn.core.http.HttpClientFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

/**
 * @author dongy
 * @date 16:10 2022/2/16
 **/
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigCodec {


    private static final String PRIVATE_ENCRYPT = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMhDE027hUiwONgAvEyhcCUG0MNcQdoYtPL5TjA36jUiFDPImVUaQOHq62DCTnhvjTrNbTBvBszGMBtBaY7C3joELcIlo6KiZFkKMeHtbhkCIsr0BWl1tmpTbxj4PCTJn0S_IK5LglqUVnQLgZbmn0m44h1uU_iH6RhA7gka6CVPAgMBAAECgYBnB4NF6aTfybHlBzPZJPFiS0haSfujLjTiEHs2eX3oShkkrx6b1v7sfAUO6cifU5zQVOHJGkXgUlJro1KgXQcg3pwRkrZ_kR096NGucFQb64CHrMJCZ_kpRo8bTFKS5t9_X0lgdlGfluUr8QuiHuuH3v4xxSctw0tyuUWkMoztaQJBAPDFDIw3GbwF4MqgJ4e3QbYUWXt8dqnahE5m6CdPZUozu95NdCmilAxTLlJ_8nCUIIAvig4c3czAqo5nArwEweMCQQDU7g6WGv7gdLFvdX0_Ej8dIXQr7wHMLG5yHqSjWtyoVWZ-MQFbavi0-G5szXjrvzLUafRFI_D5yao-MsQhm3qlAkB1jeAIfcak1MHibLugOcttmRXvgt91IunCVeA3gT5VMlkhuvNieKyML_jH8wxuBwSvUYa4SUPJ-q_Lg2QEZNY9AkEA09QYcsBce5PbyVSKyyot258iYWqe0S4KmkFd7J1CE70R_8xk2ztqPS9BO8CV4YIO4T6fIgGFhBpZ4RLEB-MTeQJBALzQfRJXqTK8fAVmczDcHWJU_GdDOpzUHuGmQ2hT8c5DleoFnz5eCycx_SN5-nMqZdq1saUgnbNsvKSFMm2Mdus";
    private static final String PUBLIC_ENCRYPT = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIQxNNu4VIsDjYALxMoXAlBtDDXEHaGLTy-U4wN-o1IhQzyJlVGkDh6utgwk54b406zW0wbwbMxjAbQWmOwt46BC3CJaOiomRZCjHh7W4ZAiLK9AVpdbZqU28Y-DwkyZ9EvyCuS4JalFZ0C4GW5p9JuOIdblP4h-kYQO4JGuglTwIDAQAB";
    private static final String RSA_ALGORITHM = "RSA";
    private static final OkHttpClient HTTP_CLIENT = HttpClientFactory.newBuilder("merchant").getOkHttpClient();

    /**
     * 解密数据
     * 通过私钥{@link ConfigCodec#PRIVATE_ENCRYPT}解密
     *
     * @param content 密文
     * @return -
     */
    public static <T> T toDecrypt(Object content, Class<T> type) {
        if (isBlank(content)) {
            return null;
        }
        RSAPrivateKey rsaPrivateKey;
        try {
            rsaPrivateKey = RSAUtil.getPrivateKey(PRIVATE_ENCRYPT);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            log.error("{}#toDecrypt fail content:{},type:{}", ConfigCodec.class, content, type);
            return null;
        }
        String decryptContent = RSAUtil.privateDecrypt(content.toString(), rsaPrivateKey);
        byte[] decode = Base64Utils.decode(decryptContent.getBytes());
        if (byte[].class.equals(type)) {
            return (T) decode;
        }
        if (String.class.equals(type)) {
            return (T) org.apache.commons.codec.binary.StringUtils.newStringUtf8(decode);
        }
        log.error("{}#toDecrypt end (type only is String or byte) content:{},type:{}", ConfigCodec.class, content, type);
        return null;
    }

    /**
     * 加密byte数据
     *
     * @param byteDate 需经过编码
     *                 string ：new String(Base64Utils.encode(opMode.toString().getBytes()))
     *                 byte[]  new String(Base64Utils.encode(bytes), StandardCharsets.UTF_8);
     */
    private static byte[] doEncrypt(byte[] byteDate) {
        if (byteDate == null || byteDate.length < 1) {
            return new byte[]{};
        }
        byte[] encodeByteDate = Base64Utils.encode(byteDate);
        // 通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new SystemException(ErrorCode.SIGN_ERROR.getCode(), "加密数据[" + new String(encodeByteDate) + "] NoSuchAlgorithmException error");
        }
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(PUBLIC_ENCRYPT));
        RSAPublicKey publicKey;
        try {
            publicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        } catch (InvalidKeySpecException e) {
            throw new SystemException(ErrorCode.SIGN_ERROR.getCode(), "加密数据[" + new String(encodeByteDate) + "] InvalidKeySpecException error");
        }
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] binaryData = RSAUtil.rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, encodeByteDate, publicKey.getModulus().bitLength());
            return Base64Utils.encode(binaryData);
        } catch (Exception e) {
            throw new SystemException(ErrorCode.SERVICE_ERROR.getCode(), "加密字符串[" + new String(encodeByteDate) + "]时遇到异常");
        }
    }

    /**
     * 加密文件
     *
     * @param value -
     * @param type  -
     * @param file  是否为文件
     * @param <T>   -
     * @return -
     */
    public static <T> T toEncrypt(String value, Class<T> type, boolean file) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        if (file) {
            InputStream inputStream;
            try {
                inputStream = Objects.requireNonNull(HTTP_CLIENT.newCall(new Request.Builder().url(value).build()).execute().body()).byteStream();
                int read = inputStream.read(bytes);
                bytes = new byte[inputStream.available()];
            } catch (IOException e) {
                throw new SystemException(ErrorCode.SIGN_ERROR.getCode(), "加密文件,读取文件流异常");
            }
        }
        byte[] bytesEncrypt = doEncrypt(bytes);
        if (String.class.equals(type)) {
            return (T) org.apache.commons.codec.binary.StringUtils.newStringUtf8(bytesEncrypt);
        }
        if (byte[].class.equals(type)) {
            return (T) bytesEncrypt;
        }
        log.error("{}#toEncrypt end (type only is String or byte) content:{},type:{}", ConfigCodec.class, Base64.encodeBase64URLSafeString(bytes), type);
        return null;
    }


    private static boolean isBlank(Object val) {
        if (val == null) {
            return true;
        }
        return !StringUtils.isNotBlank(val.toString());
    }

}
