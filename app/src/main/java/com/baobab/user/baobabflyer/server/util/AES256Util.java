package com.baobab.user.baobabflyer.server.util;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AES256Util {

    private static final String key = "kang&jang&kim-baobab0858";

    public String decrypt(String str) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, DecoderException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        if(str != null){
            final Cipher decryptCipher = Cipher.getInstance("AES");
            decryptCipher.init(Cipher.DECRYPT_MODE, generateMySQLAESKey(key, "UTF-8"));
            String decStr = new String(decryptCipher.doFinal(Hex.decodeHex(str.toCharArray())), CharEncoding.UTF_8);
            return decStr;
        }else {
            return str;
        }
    }

    public String encrypt(String str) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        if(str != null) {
            final Cipher encryptCipher = Cipher.getInstance("AES");
            encryptCipher.init(Cipher.ENCRYPT_MODE, generateMySQLAESKey(key, "UTF-8"));
            String encStr = new String(Hex.encodeHex(encryptCipher.doFinal(str.getBytes("UTF-8")))).toUpperCase();
            return encStr;
        }else {
            return str;
        }
    }

    public static SecretKeySpec generateMySQLAESKey(final String key, final String str) {
        try {
            final byte[] finalKey = new byte[16];
            int i=0;
            for(byte b : key.getBytes(str)) {
                finalKey[i++ % 16] ^= b;
            }
            return new SecretKeySpec(finalKey, "AES");
        }catch(UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}