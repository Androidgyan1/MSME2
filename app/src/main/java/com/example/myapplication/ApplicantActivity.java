package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.CrossPlatformEncryptDecrypt;

import android.util.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class ApplicantActivity extends AppCompatActivity {

    private static final String SECRET_KEY = "9NX42KXfRyF7napo7eHV5eahDTiRFiAiXC1FZq3Yk9A="; // Change as needed
    private static final byte[] SALT = new byte[]{0x49, 0x76, 0x61, 0x6e, 0x20, 0x4d, 0x65, 0x64, 0x76, 0x65, 0x64, 0x65, 0x76};

    private static final String SEARCH_URL = "http://10.135.30.111/api/AppDetails";
//    private static final String SECRET_KEY_BASE64 = "9NX42KXfRyF7napo7eHV5eahDTiRFiAiXC1FZq3Yk9A=";


    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    // Ensure this is exactly 32 characters for AES-256 (use proper key derivation in production)
    //private static final String SECRET_KEY = "9NX42KXfRyF7napo7eHV5eahDTiRFiAiXC1FZq3Yk9A=";
    private static final String IV = "1234567890123456"; // 16 bytes IV


    private EditText editTextSearch;
    private AppCompatButton searchButton;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant);

        editTextSearch = findViewById(R.id.edittextSearch);
        searchButton = findViewById(R.id.search);
        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                try {
                    String applicantId = editTextSearch.getText().toString().trim();

                    if (applicantId.isEmpty()) {
                        Toast.makeText(ApplicantActivity.this, "Please enter an Application ID", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String encryptedApplicantId = CrossPlatformEncryptDecrypt.encrypt(applicantId,SECRET_KEY);
                    Log.d("ENCRYPTED_VALUE", "Encrypted: " + encryptedApplicantId);

                    // Decrypt to verify correctness
                    String decryptedApplicantId = CrossPlatformEncryptDecrypt.decrypt(encryptedApplicantId,SECRET_KEY);
                    Log.d("DECRYPTED_VALUE", "Decrypted: " + decryptedApplicantId);

                    // Encrypt Applicant ID
                     //String encryptedApplicantId = encryptAES(applicantId);
//
//                    // Display the Encrypted Text in EditText (optional)
//                   editTextSearch.setText(encryptedApplicantId);

                    // Send Encrypted ID to Server
                    sendApplicantIdToServer(encryptedApplicantId);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ApplicantActivity.this, "Encryption failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendApplicantIdToServer(String encryptedApplicantId) {
        String token = sharedPreferences.getString("auth_token", "").trim();

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("ApplicationID", encryptedApplicantId); // Use the encrypted data

            Log.d("REQUEST_BODY", requestBody.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, SEARCH_URL, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String encryptedData = response.getString("EncryptedData");
                                String decryptedData = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    decryptedData = CrossPlatformEncryptDecrypt.decrypt(encryptedData,SECRET_KEY);
                                }
//
                                if (decryptedData != null) {

                                    try{
                                        JSONObject jsonObject = new JSONObject(decryptedData);
                                        JSONObject responseObj = jsonObject.getJSONObject("Response");

                                        // Format the data to display
                                        String displayText =responseObj.getString("ApplicantName");
                                        String displayNo =responseObj.getString("ApplicationNo");
                                        String displayCategory =responseObj.getString("Category");
                                        String displayQulification =responseObj.getString("Qualification");
                                        String displayGender =responseObj.getString("Gender");
                                        String displayEmail =responseObj.getString("Email");
                                        String displayMobileno =responseObj.getString("MobileNo");
                                        String displayPancard =responseObj.getString("Pancard");
                                        String displayIndustryActivity =responseObj.getString("IndustryActivity");
                                        String displayProjectCoast =responseObj.getString("ProjectCost");
                                        String displaySchemeCode =responseObj.getString("SchemeCode");
                                        String displayRegistrationCode =responseObj.getString("RegistrationCode");
                                        String displayFinincialId =responseObj.getString("FinanceId");
                                        String displayAccountNoBeneficiary =responseObj.getString("AccountNoBeneficiary");
                                        String displayCapitalExpenditureApproved =responseObj.getString("CapitalExpenditureApproved");
                                        String displayCapitalExpenditureFinance =responseObj.getString("CapitalExpenditureFinance");
                                        Intent i = new Intent(ApplicantActivity.this,RecycleApplicant.class);

                                        i.putExtra("ApplicantName",displayText);
                                        i.putExtra("APPLICANTNO",displayNo);
                                        i.putExtra("Category",displayCategory);
                                        i.putExtra("Qualification",displayQulification);
                                        i.putExtra("Gender",displayGender);
                                        i.putExtra("Email",displayEmail);
                                        i.putExtra("MobileNo",displayMobileno);
                                        i.putExtra("Pancard",displayPancard);
                                        i.putExtra("IndustryActivity",displayIndustryActivity);
                                        i.putExtra("ProjectCost",displayProjectCoast);
                                        i.putExtra("SchemeCode",displaySchemeCode);
                                        i.putExtra("RegistrationCode",displayRegistrationCode);
                                        i.putExtra("FinanceId",displayFinincialId);
                                        i.putExtra("AccountNoBeneficiary",displayAccountNoBeneficiary);
                                        i.putExtra("CapitalExpenditureApproved",displayCapitalExpenditureApproved);
                                        i.putExtra("CapitalExpenditureFinance",displayCapitalExpenditureFinance);
                                        startActivity(i);



                                        SharedPreferences sharedPreferencesusername = PreferenceManager.getDefaultSharedPreferences(ApplicantActivity.this);
                                        SharedPreferences.Editor editorusename = sharedPreferencesusername.edit();

                                        // Save the token value
                                        editorusename.putString("ApplicantName", displayText);
                                        editorusename.apply(); // Don't forget to apply changes!



                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ApplicantActivity.this);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        // Save the token value
                                        editor.putString("ApplicationNo", displayNo);
                                        editor.apply(); // Don't forget to apply changes!



                                        SharedPreferences sharedPreferencesscheme = PreferenceManager.getDefaultSharedPreferences(ApplicantActivity.this);
                                        SharedPreferences.Editor editorsceheme = sharedPreferencesscheme.edit();

                                        // Save the token value
                                        editorsceheme.putString("SchemeCode", displaySchemeCode);
                                        editorsceheme.apply(); // Don't forget to apply changes!



                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }


                                    Log.d("DECRYPTED_RESPONSE", decryptedData);
                                    Toast.makeText(ApplicantActivity.this, decryptedData, Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("DECRYPTION_ERROR", "Decryption failed!");
                                    Toast.makeText(ApplicantActivity.this, "Decryption failed!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null) {
                        Log.e("SERVER_ERROR", "Error Code: " + error.networkResponse.statusCode);
                        Log.e("SERVER_ERROR", "Error Data: " + new String(error.networkResponse.data));
                    } else {
                        Log.e("SERVER_ERROR", "Error: " + error.toString());
                    }
                    Toast.makeText(ApplicantActivity.this, "Server Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token.trim());
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts data using AES with CBC mode and PKCS5Padding.
     */
    // Decode Base64 key and ensure it's exactly 32 bytes
    public static String encryptAES(String plainText) {
        //String SECRET_KEY = "9NX42KXfRyF7napo7eHV5eahDTiRFiAiXC1FZq3Yk9A=";
        try {
            // Decode the Base64-encoded key to get the raw AES key
            byte[] keyBytes = Base64.decode(SECRET_KEY, Base64.NO_WRAP);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // Encrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return "Encryption Error!";
        }
    }


    public static String decryptAES(String cipherText) {
        try {
            cipherText = cipherText.replace(" ", "+");
            byte[] encryptedBytes = Base64.decode(cipherText, Base64.NO_WRAP);

            // Decode the Base64-encoded key
            byte[] keyBytes = Base64.decode(SECRET_KEY, Base64.NO_WRAP);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // Decrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "Decryption Error!";
        }
    }


//    public static String encryptString21(String strToEncrypt, String key)
//    {
//        try {
//            String salt = "0000000011111111";
//
//            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1 };
//            IvParameterSpec ivspec = new IvParameterSpec(iv);
//            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
//            KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 1000, 256);
//            SecretKey tmp = factory.generateSecret(spec);
//            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
//            Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                return java.util.Base64.getEncoder().encodeToString( cipher.doFinal(strToEncrypt.getBytes(
//                        StandardCharsets.UTF_8)));
//            }
//        }
//        catch (Exception e) {
//            System.out.println("Error while encrypting: " + e.toString());
//        }
//        return null;
//    }

//    public static String decryptString21(String strToDecrypt, String key)
//    {
//        try {
//            String salt = "0000000011111111";
//
//            byte[] iv ={ 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1 };
//            IvParameterSpec ivspec = new IvParameterSpec(iv);
//            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
//
//            KeySpec spec = new PBEKeySpec(key.toCharArray(), salt.getBytes(), 1000, 256);
//            SecretKey tmp = factory.generateSecret(spec);
//            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
//            Cipher cipher = Cipher.getInstance( "AES/CBC/PKCS5PADDING");
//            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
//            // ✅ FIX: Use android.util.Base64 instead of java.util.Base64
//            byte[] decodedBytes = Base64.decode(strToDecrypt, Base64.NO_WRAP);
//        }
//        catch (Exception e) {
//            System.out.println("Error while decrypting: "
//                    + e.toString());
//        }
//        return null;
//
//    }

    }

//    private static SecretKeySpec generateSecretKey(String password, byte[] salt) throws Exception {
//        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1000, 256);
//        SecretKey secretKey = factory.generateSecret(spec);
//        return new SecretKeySpec(secretKey.getEncoded(), "AES");
//    }

