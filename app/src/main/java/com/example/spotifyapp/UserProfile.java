package com.example.spotifyapp;


public class UserProfile {
    private String accessToken;
    private String accessCode;
    private String profileInfo; // This could be a JSON string or a more structured object depending on your needs

    private String spotifyEmail;
    private String topTracksJsonString; //now a string

    private int lastUpdatedYear;

    public UserProfile() {} // Empty constructor needed for Firestore

    public UserProfile(String accessToken, String accessCode, String profileInfo, String spotifyEmail, String topTracksJsonString, int lastUpdatedYear) {
        this.accessToken = accessToken;
        this.accessCode = accessCode;
        this.profileInfo = profileInfo;
        this.spotifyEmail = spotifyEmail;
        this.topTracksJsonString = topTracksJsonString;
        this.lastUpdatedYear = lastUpdatedYear;
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

    public String getSpotifyEmail() {
        return spotifyEmail;
    }

    public void setSpotifyEmail(String spotifyEmail) {
        this.spotifyEmail = spotifyEmail;
    }

    public String getTopTracksJsonString() {
        return topTracksJsonString;
    }

    public void setTopTracksJsonString(String topTracksJsonString) {
        this.topTracksJsonString = topTracksJsonString;
    }

    public int getlastUpdatedYear() {
        return lastUpdatedYear;
    }

    public void setLastUpdatedYear(int lastUpdatedYear) {
        this.lastUpdatedYear = lastUpdatedYear;
    }
}
