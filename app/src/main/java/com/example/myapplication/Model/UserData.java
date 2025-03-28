package com.example.myapplication.Model;

public class UserData {
    private String schemeName;
    private String applicationId;
    private String email;

    public UserData(String schemeName, String applicationId, String email) {
        this.schemeName = schemeName;
        this.applicationId = applicationId;
        this.email = email;
    }

    public String getSchemeName() { return schemeName; }
    public String getApplicationId() { return applicationId; }
    public String getEmail() { return email; }
}