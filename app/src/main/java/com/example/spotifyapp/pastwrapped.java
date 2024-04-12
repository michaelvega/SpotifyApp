package com.example.spotifyapp;


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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class pastwrapped extends BaseActivity {

    private TextView songName1, songName2, songName3;
    private Button playButton1, playButton2, playButton3;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Initialize Firestore instance

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
            }
        });
        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to fetch and show the data
                fetchAndShowTimeRangeTopTracks("monthlyTopTracks", "pastSixMonths");
            }
        });
        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the method to fetch and show the data
                fetchAndShowTimeRangeTopTracks("weeklyTopTracks", "pastFourWeeks");
            }
        });
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
        // Implement the logic to play the song using Spotify SDK or an Intent if possible
        Log.d("PlaySong", "Playing song URI: " + uri);
        // For example, to open in Spotify app:

    }
}