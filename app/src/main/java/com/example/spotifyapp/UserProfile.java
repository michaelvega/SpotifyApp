package com.example.spotifyapp;

public class UserProfile {
    private String accessToken;
    private String accessCode;
    private String profileInfo; // This could be a JSON string or a more structured object depending on your needs

    public UserProfile() {} // Empty constructor needed for Firestore

    public UserProfile(String accessToken, String accessCode, String profileInfo) {
        this.accessToken = accessToken;
        this.accessCode = accessCode;
        this.profileInfo = profileInfo;
    }

    // Getters and setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getProfileInfo() {
        return profileInfo;
    }

    public void setProfileInfo(String profileInfo) {
        this.profileInfo = profileInfo;
    }
}
