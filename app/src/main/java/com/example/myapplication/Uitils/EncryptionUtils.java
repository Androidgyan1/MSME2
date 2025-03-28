package com.example.myapplication.Uitils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtils {

    /**
     * Generates a SecretKeySpec from a Base64-encoded key.
     *
     * @param base64Key The Base64-encoded key.
     * @return A SecretKeySpec object.
     */
    public static SecretKeySpec generateSecretKey(String base64Key) {
        // Decode the Base64 key into a byte array
        byte[] decodedKey = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            decodedKey = Base64.getDecoder().decode(base64Key);
        }

        // Create a SecretKeySpec using the decoded key and the AES algorithm
        return new SecretKeySpec(decodedKey, "AES");
    }

    /**
     * Encrypts data using AES encryption in CBC mode.
     *
     * @param plainText The data to encrypt.
     * @param secretKey The secret key for encryption.
     * @return A Base64-encoded string containing the IV + encrypted data.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptAES(String plainText, SecretKeySpec secretKey) throws Exception {
        // Generate a random IV
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // Initialize the cipher in encryption mode
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        // Encrypt the data
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combine IV + encrypted data
        byte[] combined = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

        // Encode the combined data in Base64
        return Base64.getEncoder().encodeToString(combined);
    }
}
