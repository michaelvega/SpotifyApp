package com.example.spotifyapp;


import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class wrapped extends BaseActivity {

    private String accessToken = "";
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private TextView songName1, songName2, songName3;
    private Button playButton1, playButton2, playButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped);

        initializeDrawer();

        fetchTopThreeSongs();

        songName1 = findViewById(R.id.songName1);
        songName2 = findViewById(R.id.songName2);
        songName3 = findViewById(R.id.songName3);

        playButton1 = findViewById(R.id.playButton1);
        playButton2 = findViewById(R.id.playButton2);
        playButton3 = findViewById(R.id.playButton3);

        playButton1.setEnabled(false);
        playButton2.setEnabled(false);
        playButton3.setEnabled(false);

        // Set up click listeners for the play buttons
        playButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSong(playButton1);

            }
        });

        // Repeat for playButton2 and playButton3
    }


    private void fetchAccessTokenFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = login.mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference docRef = db.collection("users").document(userId);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Extract the access token from the document
                    accessToken = document.getString("accessToken");
                    if (accessToken != null) {
                        // Do something with the accessToken, for example:
                        Log.d("Firebase", "Access Token: " + accessToken);
                        Toast.makeText(wrapped.this, "Access Token: " + accessToken, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(wrapped.this, "Access Token not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(wrapped.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(wrapped.this, "Failed to fetch access token: " + task.getException(), Toast.LENGTH_SHORT).show();
                Log.d("Firebase", "Error getting document: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(wrapped.this, "Error fetching from Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Firebase", "Error fetching accessToken", e);
        });
    }


    private void fetchTopThreeSongs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = login.mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference docRef = db.collection("users").document(userId)
                .collection("yearlyTopTracks").document(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    String topTracksJsonString = document.getString("topTracksJsonString");
                    if (topTracksJsonString != null) {
                        try {
                            JSONObject topTracksJson = new JSONObject(topTracksJsonString);
                            JSONArray items = topTracksJson.getJSONArray("items");
                            if (items.length() >= 3) {
                                // Extract the URIs and song names for the top three tracks
                                String uri1 = items.getJSONObject(0).getString("uri");
                                String name1 = items.getJSONObject(0).getString("name");
                                String uri2 = items.getJSONObject(1).getString("uri");
                                String name2 = items.getJSONObject(1).getString("name");
                                String uri3 = items.getJSONObject(2).getString("uri");
                                String name3 = items.getJSONObject(2).getString("name");
                                // Update the UI with the song names and set the URIs as tags for the buttons
                                updateSongNamesAndSetUris(name1, uri1, name2, uri2, name3, uri3);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(wrapped.this, "Failed to parse top tracks JSON", Toast.LENGTH_SHORT).show();
                            Log.e("JSON", "Failed to parse top tracks JSON", e);
                        }
                    } else {
                        Toast.makeText(wrapped.this, "Top tracks not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(wrapped.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(wrapped.this, "Failed to fetch top tracks: " + task.getException(), Toast.LENGTH_SHORT).show();
                Log.d("Firebase", "Error getting document: ", task.getException());
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(wrapped.this, "Error fetching from Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Firebase", "Error fetching top tracks", e);
        });
    }


// Don't forget to adjust your updateSongNamesAndSetUris function accordingly if you need to display album names or URIs in your UI.


    private void updateSongNamesAndSetUris(String name1, String uri1, String name2, String uri2, String name3, String uri3) {
        runOnUiThread(() -> {
            songName1.setText(name1);
            songName2.setText(name2);
            songName3.setText(name3);
            playButton1.setTag(uri1);
            playButton2.setTag(uri2);
            playButton3.setTag(uri3);
            playButton1.setEnabled(true);
            playButton2.setEnabled(true);
            playButton3.setEnabled(true);
        });
    }

    private void playSong(Button playButton) {
        String songUri = (String) playButton.getTag();
        if (songUri == null) {
            Toast.makeText(this, "Song URI not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Request playRequest = new Request.Builder()
                .url("https://api.spotify.com/v1/me/player/play")
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(RequestBody.create("{\"uris\": [\"" + songUri + "\"]}", MediaType.parse("application/json")))
                .build();

        mOkHttpClient.newCall(playRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(wrapped.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Handle response
            }
        });
    }




    // Update the TextViews with the song names when you fetch the top tracks
    private void updateSongNames() {
        // Set the text of songName1, songName2, and songName3 to the names of the top tracks
    }
}
