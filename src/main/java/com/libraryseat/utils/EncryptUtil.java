package com.libraryseat.utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Base64;

public class EncryptUtil {
    public static final String ALGORITHM = "HmacSHA256";

    public static String base64Encode(String str) {
        if (str == null)
            return null;
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }
    /**Base64解码，支持去掉末尾等号的密文。*/
    public static String base64Decode(String base64) {
        if(base64 == null)
            return null;
        byte[] bytes;
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            bytes = decoder.decode(base64);
        } catch (IllegalArgumentException e) {
            try {
                base64 += '=';
                bytes = decoder.decode(base64);
            } catch (IllegalArgumentException ex) {
                bytes = decoder.decode(base64+'=');
            }
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * hmac加密，返回byte[]。或许返回String的那个方法才是你想要的。
     */
    public static byte[] encrypt(byte[] text, byte[] key) throws GeneralSecurityException {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKey secretKey = new SecretKeySpec(key,ALGORITHM);
        mac.init(secretKey);
        return mac.doFinal(text);
    }

    /**
     * hmac加密
     * @param text 要加密的内容.
     * @param key 加密密钥。必须是ISO-8859-1可编码字符串，即每个字符codepoint均在[0,255]之间
     * @param textCharset text的字符集。通常为utf-8.
     * @return 加密结果，以16进制字符串展示.密钥不合法返回null
     */
    public static String encrypt(String text, String key, Charset textCharset) {
        try {
            byte[] bytes = encrypt(text.getBytes(textCharset), key.getBytes(StandardCharsets.ISO_8859_1));
            return byteArrayToHexString(bytes);
        }catch (GeneralSecurityException e) {
            return null;
        }
    }

    static byte[] md5(byte[] text){
        if (text == null)
            return null;
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(text);
            return digest.digest();
        } catch (GeneralSecurityException e) {
            return null;
        }
    }

    static String byteArrayToHexString(byte[] bytes) {
        if (bytes == null)
            return null;
        StringBuilder hex = new StringBuilder(bytes.length);
        for (byte b : bytes) {
            String temp = Integer.toHexString(b & 0xFF);
            if (temp.length() == 1) {
                //1得到一位的进行补0操作
                hex.append('0');
            }
            hex.append(temp);
        }
        return hex.toString();
    }
}
