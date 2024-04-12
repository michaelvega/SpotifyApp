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


    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private TextView songName1, songName2, songName3;
    private Button playButton1, playButton2, playButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wrapped);

        initializeDrawer();

        songName1 = findViewById(R.id.songName1);
        songName2 = findViewById(R.id.songName2);
        songName3 = findViewById(R.id.songName3);

        playButton1 = findViewById(R.id.playButton1);
        playButton2 = findViewById(R.id.playButton2);
        playButton3 = findViewById(R.id.playButton3);

        // Set up click listeners for the play buttons
        playButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playSong(0); // Assuming 0 is the index of the first song in your list
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
                        String accessToken = document.getString("accessToken");
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



    // Update the TextViews with the song names when you fetch the top tracks
    private void updateSongNames() {
        // Set the text of songName1, songName2, and songName3 to the names of the top tracks
    }
}
