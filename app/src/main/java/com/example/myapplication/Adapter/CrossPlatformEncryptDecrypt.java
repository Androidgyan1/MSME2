package com.example.myapplication.Adapter;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CrossPlatformEncryptDecrypt {

    private static final byte[] SALT = {0x49, 0x76, 0x61, 0x6e, 0x20, 0x4d, 0x65, 0x64, 0x76, 0x65, 0x64, 0x65, 0x76};

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String clearText, String encryptionKey) throws Exception {
        byte[] clearBytes = clearText.getBytes("UTF-8");
        byte[] key = generateKey(encryptionKey);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16]; // Fixed IV: all zeros
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        System.out.println("Key (Base64): " + Base64.getEncoder().encodeToString(key)); // Log the derived key
        System.out.println("IV (Base64): " + Base64.getEncoder().encodeToString(iv));

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        byte[] encryptedBytes = cipher.doFinal(clearBytes);

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String cipherText, String encryptionKey) throws Exception {
        byte[] cipherBytes = Base64.getDecoder().decode(cipherText);
        byte[] key = generateKey(encryptionKey);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[16]; // Fixed IV: all zeros
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(cipherBytes);

        return new String(decryptedBytes, "UTF-8");
    }

    private static byte[] generateKey(String encryptionKey) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(encryptionKey.toCharArray(), SALT, 1000, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }
}