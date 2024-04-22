package com.example.spotifyapp;

import androidx.annotation.NonNull;


import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.WriteBatch;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.google.firebase.firestore.FirebaseFirestore;


public class login extends BaseActivity {

    public static final String CLIENT_ID = "66543f1060f94bde954afafe1e5ce2ae";
    public static final String REDIRECT_URI = "spotifyapp://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessCode;


    private String spotifyEmail;

    private JSONObject topTracksJson;

    private String topTracksJsonString;

    private String topThreeLong, topThreeMedium, topThreeShort;
    private Call mCall;

    public static FirebaseAuth mAuth;

    private TextView tokenTextView, codeTextView, profileTextView;

    private TextView musicListeningText;

    private TextView directions;
    private Button musicListeningBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        initializeDrawer();


        // Initialize the views
        tokenTextView = (TextView) findViewById(R.id.token_text_view);
        codeTextView = (TextView) findViewById(R.id.code_text_view);
        profileTextView = (TextView) findViewById(R.id.response_text_view);
        directions = findViewById(R.id.directions);

        // Initialize the buttons
        Button tokenBtn = (Button) findViewById(R.id.token_btn);
        Button codeBtn = (Button) findViewById(R.id.code_btn);
        Button profileBtn = (Button) findViewById(R.id.profile_btn);
        musicListeningText = findViewById(R.id.music_listening_text);
        musicListeningBtn = findViewById(R.id.music_listening_btn);

        musicListeningBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchMusicListeningHabits();
            }
        });

        Button saveToFirestoreBtn = findViewById(R.id.save_to_firestore_btn);
        saveToFirestoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserProfileToFirestore();
            }
        });

        // Set the click listeners for the buttons

        tokenBtn.setOnClickListener((v) -> {
            getToken();
        });

        codeBtn.setOnClickListener((v) -> {
            getCode();
        });

        profileBtn.setOnClickListener((v) -> {
            onGetUserProfileClicked();
        });

    }

    /**
     * Get token from Spotify
     * This method will open the Spotify login activity and get the token
     * What is token?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(login.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    /**
     * Get code from Spotify
     * This method will open the Spotify login activity and get the code
     * What is code?
     * https://developer.spotify.com/documentation/general/guides/authorization-guide/
     */
    public void getCode() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.CODE);
        AuthorizationClient.openLoginActivity(login.this, AUTH_CODE_REQUEST_CODE, request);
    }


    /**
     * When the app leaves this activity to momentarily get a token/code, this function
     * fetches the result of that external activity to get the response from Spotify
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            setmAccessToken(response.getAccessToken());
            Log.d("access token", getmAccessToken());
            setTextAsync(getmAccessToken(), tokenTextView);

        } else if (AUTH_CODE_REQUEST_CODE == requestCode) {
            mAccessCode = response.getCode();
            setTextAsync(mAccessCode, codeTextView);
        }
    }

    /**
     * Get user profile
     * This method will get the user profile using the token
     */
    public void onGetUserProfileClicked() {
        if (getmAccessToken() == null) {
            Toast.makeText(this, "You need to get an access token first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a request to get the user profile
        final Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me")
                .addHeader("Authorization", "Bearer " + getmAccessToken())
                .build();

        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("HTTP", "Failed to fetch data: " + e);
                Toast.makeText(login.this, "Failed to fetch data, watch Logcat for more details",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    spotifyEmail = jsonObject.optString("email", "No spotifyEmail found");
                    final String humanReadableJSON = jsonObject.toString(4);
                    //setTextAsync("Email: " + spotifyEmail + "\n\nData: " + humanReadableJSON, profileTextView);
                    setTextAsync("Synced!", profileTextView);
                } catch (JSONException e) {
                    Log.d("JSON", "Failed to parse data: " + e);
                    Toast.makeText(login.this, "Failed to parse data, watch Logcat for more details",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchMusicListeningHabits() {
        if (getmAccessToken() == null) {
            Toast.makeText(this, "Access Token not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Define the time ranges for which you want to fetch the top tracks
        String[] timeRanges = {"long_term", "medium_term", "short_term"};

        for (String timeRange : timeRanges) {
            String url = "https://api.spotify.com/v1/me/top/tracks?fields=items(uri,name,album(name,href),artists(name,href))&time_range=" + timeRange + "&limit=3";

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + getmAccessToken())
                    .build();

            mOkHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Spotify API", "Failed to fetch top tracks for time range: " + timeRange, e);
                    // Handle error, possibly update UI thread with Toast
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        final String responseBody = response.body().string();
                        Log.d("top tracks", responseBody);

                        try {
                            JSONObject jsonObject = new JSONObject(responseBody);
                            JSONArray items = jsonObject.getJSONArray("items");
                            JSONArray tracksJsonArray = new JSONArray();  // Array to hold JSON objects for each track

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject track = items.getJSONObject(i);
                                // Get track name
                                String trackName = track.getString("name");
                                // Get track URI (Assuming the URI is a property of the track object, adjust if it's nested deeper)
                                String trackUri = track.getString("uri");

                                // Create a new JSON object and put the track name and URI into it
                                JSONObject trackJson = new JSONObject();
                                trackJson.put("name", trackName);
                                trackJson.put("uri", trackUri);

                                // Add the JSON object to the JSON array
                                tracksJsonArray.put(trackJson);
                            }

                            // Convert the JSON array to string if needed or directly use it
                            final String tracksJsonString = tracksJsonArray.toString();

                            runOnUiThread(() -> {
                                switch (timeRange) {
                                    case "long_term":
                                        topThreeLong = tracksJsonString;
                                        break;
                                    case "medium_term":
                                        topThreeMedium = tracksJsonString;
                                        break;
                                    case "short_term":
                                        topThreeShort = tracksJsonString;
                                        break;
                                }
                                // Optional: Update the UI here if necessary
                            });

                        } catch (JSONException e) {
                            Log.e("JSON parsing", "Failed to parse JSON", e);
                            // Handle the error gracefully, possibly update UI thread with Toast
                        }
                    } else {
                        Log.e("HTTP error", "Server responded with error for time range: " + timeRange);
                        // Handle HTTP error, possibly update UI thread with Toast
                    }
                }
            });
        }

        String urlAllTime = "https://api.spotify.com/v1/me/top/tracks?fields=items(name,album(name,href),artists(name,href))"; // Adjust limit as necessary
        Request requestAllTime = new Request.Builder()
                .url(urlAllTime)
                .addHeader("Authorization", "Bearer " + getmAccessToken())
                .build();

        mOkHttpClient.newCall(requestAllTime).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Spotify API", "Failed to fetch all top tracks", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    topTracksJsonString = responseBody.toString(); // Storing the entire response
                    Log.d("All Top Tracks", responseBody);
                    // Optionally parse and update UI here
                    runOnUiThread(() -> setTextAsync("Synced!", musicListeningText));
                } else {
                    Log.e("HTTP error", "Server responded with error while fetching all top tracks");
                }
            }
        });
    }


    private void saveUserProfileToFirestore() {
        // Create an instance of the UserProfile model with your data

        UserProfile userProfile = null;

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        try {
            userProfile = new UserProfile(getmAccessToken(), mAccessCode, profileTextView.getText().toString(), spotifyEmail, topTracksJsonString, year);
        } catch (Exception e) {
            // Toast.makeText(this, "A field is incomplete or sync failed", Toast.LENGTH_SHORT).show();
            Log.w("error code: ", e);
            return;
        }

        Map<String, Object> yearlyInfo = new HashMap<>();
        yearlyInfo.put("topThreeTracks", topThreeLong); // Saving as a JSON string
        yearlyInfo.put("timeRange", "pastYear"); // Adding the year field

        Map<String, Object> monthlyInfo = new HashMap<>();
        monthlyInfo.put("topThreeTracks", topThreeMedium); // Saving as a JSON string
        monthlyInfo.put("timeRange", "pastSixMonths"); // Adding the year field

        Map<String, Object> weeklyInfo = new HashMap<>();
        weeklyInfo.put("topThreeTracks", topThreeShort); // Saving as a JSON string
        weeklyInfo.put("timeRange", "pastFourWeeks"); // Adding the year field

        // Get an instance of the Firestore database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();

        DocumentReference userProfileRef = db.collection("users").document(userId);
        DocumentReference yearlyTopTracksRef = userProfileRef.collection("yearlyTopTracks").document("pastYear");
        DocumentReference monthlyTopTracksRef = userProfileRef.collection("monthlyTopTracks").document("pastSixMonths");
        DocumentReference weeklyTopTracksRef = userProfileRef.collection("weeklyTopTracks").document("pastFourWeeks");



        WriteBatch batch = db.batch();
        batch.set(userProfileRef, userProfile); // Update the user profile
        batch.set(yearlyTopTracksRef, yearlyInfo); // Add or update the yearly top tracks
        batch.set(monthlyTopTracksRef, monthlyInfo);
        batch.set(weeklyTopTracksRef, weeklyInfo);


        if ( userProfile.getAccessToken() != null && userProfile.getAccessCode() != null && userProfile.getProfileInfo() != null && userProfile.getSpotifyEmail() != null && userProfile.getTopTracksJsonString() != null){
            // Get or create a "users" collection in your Firestore database
            // Use a unique identifier for each user document, here I'm using the access code but you might want to use something like Firebase Authentication UID
            batch.commit()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(login.this, "User profile saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(login.this, "Error saving user profile", Toast.LENGTH_SHORT).show();
                            Log.d("Firestore", "Error adding document", e);
                        }
                    });
        } else {
            Toast.makeText(this, "A field is incomplete or sync failed, try again", Toast.LENGTH_SHORT).show();
            Log.d("sync failed", "A field is incomplete or sync failed");
            return;
        }

    }

    /**
     * Creates a UI thread to update a TextView in the background
     * Reduces UI latency and makes the system perform more consistently
     *
     * @param text the text to set
     * @param textView TextView object to update
     */
    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    /**
     * Get authentication request
     *
     * @param type the type of the request
     * @return the authentication request
     */
    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    /**
     * Gets the redirect Uri for Spotify
     *
     * @return redirect Uri object
     */
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }


}