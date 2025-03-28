package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.Adapter.CrossPlatformEncryptDecrypt;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class CameraActivity extends AppCompatActivity {

    // Constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int LOCATION_PERMISSION_CODE = 102;
    private static final String SECRET_KEY = "9NX42KXfRyF7napo7eHV5eahDTiRFiAiXC1FZq3Yk9A="; // Must be 32 bytes

    // UI Components
    private ImageView photoImageView1, photoImageView2, photoImageView3;
    private TextView messageTextView;
    private Button sendButton;

    // Data
    private Bitmap photo1, photo2, photo3;
    private String latitude = "0.0", longitude = "0.0";
    private String userName, displayNo, schemeCode;
    private ImageView lastClickedImageView;

    // Services
    private FusedLocationProviderClient fusedLocationClient;
    private RequestQueue requestQueue;
    private SharedPreferences sharedPreferences;

    String ApplicantNO,SchemeCode1,ApplicantName;

    String Image1 ="/9j/4AAQSkZJRgABAQEBLAEsAAD/4RNARXhpZgAATU0AKgAAAAgACgEPAAIAAAAGAAAAhgEQAAIAAAAQAAAAjAESAAMAAAABAAEAAAEaAAUAAAABAAAAnAEbAAUAAAABAAAApAEoAAMAAAABAAIAAAExAAIAAAAcAAAArAEyAAIAAAAUAAAAyAITAAMAA";
String image2 = "/9j/4AAQSkZJRgABAQEBLAEsAAD/4RNARXhpZgAATU0AKgAAAAgACgEPAAIAAAAGAAAAhgEQAAIAAAAQAAAAjAESAAMAAAABAAEAAAEaAAUAAAABAAAAnAEbAAUAAAABAAAApAEoAAMAAAABAAIAAAExAAIAAAAcAAAArAEyAAIAAAAUAAAAyAITAAMAA";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Initialize services first
        initializeServices();

        // Then setup UI components
        initializeViews();

        // Check permissions
        checkPermissions();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeServices() {
        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize Volley request queue
        requestQueue = Volley.newRequestQueue(this);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
/// Applicant No
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ApplicantNO = sharedPreferences.getString("ApplicationNo", "N/A");

        /// / Scheme code

        SharedPreferences sharedPreferencessceheme = PreferenceManager.getDefaultSharedPreferences(this);
        SchemeCode1 = sharedPreferencessceheme.getString("SchemeCode", "N/A");


        /// //UserName
        SharedPreferences sharedPreferencesscehemeuserName = PreferenceManager.getDefaultSharedPreferences(this);
        ApplicantName = sharedPreferencesscehemeuserName.getString("ApplicantName", "N/A");



        // Load user data
//        userName = sharedPreferences.getString("UserName", "");
//        displayNo = sharedPreferences.getString("ApplicationNo", "");
//        schemeCode = sharedPreferences.getString("SchemeCode", "");
    }

    private void initializeViews() {
        photoImageView1 = findViewById(R.id.photoImageView1);
        photoImageView2 = findViewById(R.id.photoImageView2);
        photoImageView3 = findViewById(R.id.photoImageView3);
        messageTextView = findViewById(R.id.messageTextView);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setEnabled(false);
    }

    private void checkPermissions() {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }

        // Check location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_CODE);
        } else {
            // Only get location if permission is granted
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        // Double-check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Ensure fusedLocationClient is initialized
        if (fusedLocationClient == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLongitude());
                        Log.d("Location", "Lat: " + latitude + ", Long: " + longitude);
                    } else {
                        Log.d("Location", "Location is null");
                        Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Location", "Error getting location", e);
                    Toast.makeText(this, "Error getting location: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupClickListeners() {
        photoImageView1.setOnClickListener(v -> {
            lastClickedImageView = photoImageView1;
            openCamera();
        });
        photoImageView2.setOnClickListener(v -> {
            lastClickedImageView = photoImageView2;
            openCamera();
        });
        photoImageView3.setOnClickListener(v -> {
            lastClickedImageView = photoImageView3;
            openCamera();
        });
        sendButton.setOnClickListener(v -> sendEncryptedDataToServer());
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");

            if (lastClickedImageView != null && photo != null) {
                lastClickedImageView.setImageBitmap(photo);

                // Store the photo in the correct variable
                if (lastClickedImageView == photoImageView1) {
                    photo1 = photo;
                } else if (lastClickedImageView == photoImageView2) {
                    photo2 = photo;
                } else if (lastClickedImageView == photoImageView3) {
                    photo3 = photo;
                }

                updatePhotoCount();
                lastClickedImageView = null;
            }
        }
    }

    private void updatePhotoStorage(ImageView targetView, Bitmap photo) {
        if (targetView == photoImageView1) {
            photo1 = photo;
        } else if (targetView == photoImageView2) {
            photo2 = photo;
        } else if (targetView == photoImageView3) {
            photo3 = photo;
        }
    }

    private void updatePhotoCount() {
        int count = (photo1 != null ? 1 : 0) +
                (photo2 != null ? 1 : 0) +
                (photo3 != null ? 1 : 0);

        if (count == 3) {
            messageTextView.setText("All photos captured. Ready to send!");
            sendButton.setEnabled(true);
        } else {
            messageTextView.setText(String.format("Capture %d more photos", 3 - count));
            sendButton.setEnabled(false);
        }
    }

    private void sendEncryptedDataToServer() {
        try {
            // Validate we have all required data
            if (photo1 == null || photo2 == null || photo3 == null) {
                Toast.makeText(this, "Please capture all photos first", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. Create JSON payload
            JSONObject payload = new JSONObject();
            payload.put("Latitude", Double.parseDouble(latitude));
            payload.put("Longitude", Double.parseDouble(longitude));
            payload.put("ApplicationNumber", ApplicantNO);
            payload.put("SchemeCode", SchemeCode1);
            payload.put("MacAddress", "02:00:00:00:00:00");
            payload.put("UpdatedBy", ApplicantName);

            // Encode each image separately
            String image1Base64 = encodeImage1();
            String image2Base64 = encodeImage2();
            String image3Base64 = encodeImage3();



            payload.put("Image3", image3Base64);
            payload.put("Image1", image1Base64);
            payload.put("Image2", image2Base64);


            Log.d("DisplayNo", ApplicantNO.toString());
            Log.d("Image1", image1Base64);
            Log.d("Image2", image2);
            Log.d("Image3", image3Base64);
            Log.d("SchemeCode", SchemeCode1.toString());
            Log.d("MAC_Address", "02:00:00:00:00:00");
            Log.d("UserName", ApplicantName.toString());

            // Log each image separately with their first 20 characters for verification
            Log.d("Image1_Base64", "Length: " + image1Base64.length() + ", Start: " + image1Base64.substring(0, Math.min(20, image1Base64.length())));
            Log.d("Image2_Base64", "Length: " + image2Base64.length() + ", Start: " + image2Base64.substring(0, Math.min(20, image2Base64.length())));
            Log.d("Image3_Base64", "Length: " + image3Base64.length() + ", Start: " + image3Base64.substring(0, Math.min(20, image3Base64.length())));



            Log.d("REQUEST_PAYLOAD", String.valueOf(payload));

            // 2. Encrypt the entire JSON
            String encryptedData = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                encryptedData = CrossPlatformEncryptDecrypt.encrypt(payload.toString(), SECRET_KEY);
            }
            Log.d("ENCRYPTED_DATA", encryptedData);

            // 3. Create final request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("UserId", ApplicantName);
            requestBody.put("RequestData", encryptedData);

            // 4. Send to server
            sendVolleyRequest(requestBody);

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("SEND_ERROR", e.getMessage(), e);
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            Log.e("ENCODE_IMAGE", "Error closing stream", e);
        }
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }
    private void sendVolleyRequest(JSONObject requestBody) {
        String url = "http://10.135.30.111/api/imagesPush";
        String authToken = sharedPreferences.getString("auth_token", "");
        Log.d("auth_token",authToken);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    try {
                        Log.d("SERVER_RESPONSE", response.toString());
                        if (response.has("EncryptedData")) {
                            String encryptedResponse = response.getString("EncryptedData");

                            Toast.makeText(CameraActivity.this,encryptedResponse,Toast.LENGTH_SHORT).show();
                            String decryptedResponse = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                decryptedResponse = CrossPlatformEncryptDecrypt.decrypt(encryptedResponse, SECRET_KEY);
                            }
                            JSONObject jsonResponse = new JSONObject(decryptedResponse);
                           // processSuccessfulResponse(jsonResponse);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                        Log.e("RESPONSE_ERROR", e.getMessage(), e);
                    }
                },
                error -> {
                    String errorMsg = "Network error";
                    if (error.networkResponse != null) {
                        errorMsg = new String(error.networkResponse.data);
                        Log.e("NETWORK_ERROR", "Status: " + error.networkResponse.statusCode);
                    }
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + authToken);
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    private void processSuccessfulResponse(JSONObject jsonResponse) throws JSONException {
        if (jsonResponse.has("Response")) {
            JSONObject responseObj = jsonResponse.getJSONObject("Response");
            if (responseObj.has("UserName")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("UserName", responseObj.getString("UserName"));
                editor.apply();
            }
            Toast.makeText(this, "Upload successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ApplicantActivity.class));
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }
    private String encodeImage1() {
        if (photo1 == null) return "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo1.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            Log.e("ENCODE_IMAGE1", "Error closing stream", e);
        }
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    private String encodeImage2() {
        if (photo2 == null) return "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo2.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            Log.e("ENCODE_IMAGE2", "Error closing stream", e);
        }
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }

    private String encodeImage3() {
        if (photo3 == null) return "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo3.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        try {
            baos.close();
        } catch (Exception e) {
            Log.e("ENCODE_IMAGE3", "Error closing stream", e);
        }
        return Base64.encodeToString(imageBytes, Base64.NO_WRAP);
    }



}