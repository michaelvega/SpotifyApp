package com.example.spotifyapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;


public class pastwrapped extends BaseActivity {

    public static final String CLIENT_ID = "66543f1060f94bde954afafe1e5ce2ae";
    public static final String REDIRECT_URI = "spotifyapp://auth";

    public static final int AUTH_TOKEN_REQUEST_CODE = 0;
    public static final int AUTH_CODE_REQUEST_CODE = 1;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessCode;

    private TextView songName1, songName2, songName3;
    private Button playButton1, playButton2, playButton3;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Initialize Firestore instance

    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pastwrapped);

        initializeDrawer();

        songName1 = findViewById(R.id.songName1);
        songName2 = findViewById(R.id.songName2);
        songName3 = findViewById(R.id.songName3);
        playButton1 = findViewById(R.id.playButton1);
        playButton2 = findViewById(R.id.playButton2);
        playButton3 = findViewById(R.id.playButton3);

        // Initialize the year button and set an OnClickListener
        Button yearButton = findViewById(R.id.yearbutton);
        Button monthButton = findViewById(R.id.monthbutton);
        Button weekButton = findViewById(R.id.weekbutton);
        yearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to fetch and show the data
                fetchAndShowTimeRangeTopTracks("yearlyTopTracks", "pastYear");
                getToken();
            }
        });
        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to fetch and show the data
                fetchAndShowTimeRangeTopTracks("monthlyTopTracks", "pastSixMonths");
                getToken();
            }
        });
        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to fetch and show the data
                fetchAndShowTimeRangeTopTracks("weeklyTopTracks", "pastFourWeeks");
                getToken();
            }
        });
    }

    public void getToken() {
        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(pastwrapped.this, AUTH_TOKEN_REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        // Check which request code is present (if any)
        if (AUTH_TOKEN_REQUEST_CODE == requestCode) {
            setmAccessToken(response.getAccessToken());
            Log.d("access token", getmAccessToken());
            // setTextAsync(getmAccessToken(), tokenTextView);

        }
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[] { "user-read-email", "user-top-read" }) // <--- Change the scope of your requested token here
                .setCampaign("your-campaign-token")
                .build();
    }

    private void setTextAsync(final String text, TextView textView) {
        runOnUiThread(() -> textView.setText(text));
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

    private void fetchAndShowTimeRangeTopTracks(String collection, String document) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(pastwrapped.this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the UID for the path to the user's yearly top tracks
        String userId = currentUser.getUid();
        DocumentReference timeRangeTopTracksRef = db.collection("users").document(userId).collection(collection).document(document);

        timeRangeTopTracksRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String topTracksData = documentSnapshot.getString("topThreeTracks");
                    if (topTracksData != null) {
                        try {
                            JSONArray topTracksArray = new JSONArray(topTracksData);

                            // Assuming there are always exactly 3 top tracks
                            for (int i = 0; i < topTracksArray.length(); i++) {
                                JSONObject trackObj = topTracksArray.getJSONObject(i);
                                String name = trackObj.getString("name");
                                String uri = trackObj.getString("uri");

                                // Update UI for each card with the track name and set up play button
                                if (i == 0) {
                                    songName1.setText(name);
                                    playButton1.setOnClickListener(view -> playSong(uri));
                                } else if (i == 1) {
                                    songName2.setText(name);
                                    playButton2.setOnClickListener(view -> playSong(uri));
                                } else if (i == 2) {
                                    songName3.setText(name);
                                    playButton3.setOnClickListener(view -> playSong(uri));
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("JSON", "Failed to parse top tracks data", e);
                            Toast.makeText(pastwrapped.this, "Failed to parse top tracks data", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(pastwrapped.this, "No top tracks data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(pastwrapped.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error getting documents: ", e);
            Toast.makeText(pastwrapped.this, "Error fetching time range top tracks", Toast.LENGTH_SHORT).show();
        });
    }

    private void playSong(String uri) {
        // Set the connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {

            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Log.d("MainActivity", "Connected! Yay!");

                // Now you can start playing a track
                mSpotifyAppRemote.getPlayerApi().play(uri);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e("MainActivity", throwable.getMessage(), throwable);

                // Something went wrong when attempting to connect! Handle errors here
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect from the Spotify app remote when the activity stops
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }
}