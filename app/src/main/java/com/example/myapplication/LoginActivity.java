package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.CrossPlatformEncryptDecrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {

    private static final String TOKEN_URL = "http://10.135.30.111/api/auth/login";
    private static final String LOGIN_URL = "http://10.135.30.111/api/login/UserSignIn";
    private static final String USERNAME = "san.suraj51@gmail.com";
    private static final String PASSWORD = "dZX8xnJVqNGRg1zjziZ+";

    private static final String SALT = "randomSaltValue";
    private static final String SECRET_KEY = "9NX42KXfRyF7napo7eHV5eahDTiRFiAiXC1FZq3Yk9A="; // Must be 16, 24, or 32 bytes long

    private EditText editTextUsername, editTextPassword;
    private AppCompatButton btnLogin;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextUsername = findViewById(R.id.name);
        editTextPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        requestQueue = Volley.newRequestQueue(this);
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        fetchToken(); // Get token on app start

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        loginUser();  // Wait 3 sec before login to ensure token is ready
                    }
        });
    }

    private void fetchToken() {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("UserName", USERNAME);
            requestBody.put("Password", PASSWORD);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, TOKEN_URL, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String token = response.getString("Token");

                                if (token == null || token.isEmpty()) {
                                    Log.e("TOKEN_ERROR", "Token received is NULL or EMPTY!");
                                    return;
                                }

                                Log.d("TOKEN_FETCHED", "Fetched Token: [" + token + "]");

                                // Store Token
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("auth_token", token.trim());  // Trim spaces
                                editor.apply();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TOKEN", "Error fetching token: " + error.toString());
                }
            });

            // Set Retry Policy
            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000, // 10 seconds timeout
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loginUser() {

        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String token = sharedPreferences.getString("auth_token", "").trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (token == null || token.isEmpty()) {
            Log.e("TOKEN_ERROR", "Authentication token is missing!");
            Toast.makeText(this, "Authentication token is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hash the password and salt
        String hashedPassword = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hashedPassword = hashString(password);
        }
        String hashedSalt = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hashedSalt = hashStringSalt(SALT);
        }

        // Combine hashed password and salt, then hash again
        String combinedHashInput = hashedPassword + hashedSalt;
        String finalHashedPassword = hashStringHex(combinedHashInput);

        Log.d("LOGIN_DEBUG", "Token: " + token);
        Log.d("LOGIN_DEBUG", "Username: " + username);
        Log.d("LOGIN_DEBUG", "Hashed Password: " + finalHashedPassword);
        Log.d("LOGIN_DEBUG", "Hashed Salt: " + hashedSalt);

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("UserName", username);
            requestBody.put("HashPassword", finalHashedPassword);
            requestBody.put("Salt", hashedSalt);

            Log.d("LOGIN_DEBUG", "Request Body: " + requestBody.toString());

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, LOGIN_URL, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String encryptedData = response.getString("EncryptedData");
                                String decryptedData = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    decryptedData = CrossPlatformEncryptDecrypt.decrypt(encryptedData,SECRET_KEY);
                                }

                                if (decryptedData != null) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(decryptedData);
                                        JSONObject responseObj = jsonObject.getJSONObject("Response");
                                        String displayuserId =responseObj.getString("UserName");


                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        // Save the token value
                                        editor.putString("UserName", displayuserId);
                                        editor.apply(); // Don't forget to apply changes!



                                    } catch (Exception e) {
                                        throw new RuntimeException(e);

                                    }


                                    Log.d("DECRYPTED_RESPONSE", decryptedData);
                                    Toast.makeText(LoginActivity.this, decryptedData, Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(LoginActivity.this,ApplicantActivity.class);
                                    startActivity(i);
                                } else {
                                    Log.e("DECRYPTION_ERROR", "Decryption failed!");
                                    Toast.makeText(LoginActivity.this, "Decryption failed!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error.networkResponse != null) {
                        Log.e("LOGIN_ERROR", "Error Code: " + error.networkResponse.statusCode);
                        Log.e("LOGIN_ERROR", "Error Data: " + new String(error.networkResponse.data));
                    }
                    Toast.makeText(LoginActivity.this, "Login Failed: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + token.trim());
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    headers.put("Accept", "application/json");
                    return headers;
                }
            };

            request.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String hashStringSalt(String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(salt.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String hashStringHex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptAES(String cipherText) {
        try {
            cipherText = cipherText.replace(" ", "+");
            byte[] cipherBytes = android.util.Base64.decode(cipherText, android.util.Base64.NO_WRAP);

            // Extract IV (first 16 bytes)
            byte[] iv = new byte[16];
            System.arraycopy(cipherBytes, 0, iv, 0, 16);

            // Extract encrypted data (remaining bytes)
            byte[] encryptedData = new byte[cipherBytes.length - 16];
            System.arraycopy(cipherBytes, 16, encryptedData, 0, encryptedData.length);

            // Generate key
            byte[] salt = new byte[]{0x49, 0x76, 0x61, 0x6e, 0x20, 0x4d, 0x65, 0x64, 0x76, 0x65, 0x64, 0x65, 0x76};
            SecretKeySpec secretKeySpec = generateSecretKey(SECRET_KEY, salt);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Decrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedData);
            return new String(decryptedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            e.printStackTrace();
            return "Decryption Error!";
        }
    }

    private static SecretKeySpec generateSecretKey(String password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1000, 256);
        SecretKey secretKey = factory.generateSecret(spec);
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }



}