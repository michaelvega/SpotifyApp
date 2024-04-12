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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class wrapped extends BaseActivity {

    private String accessToken = "BQD82F_XVPw1MvrlHE8yr0JSA0yEle9mqCjpTSiat1FlTBoxDcU8UQGMyIe3-aqJvyUtZlE435Ge6dkUdGpux6mk0kCb2z3qrt-TUQ9czlPxF9rSblpmDoC_570htawzKDV9NZiVlYc3W5-gTreAHgEey3SC-uIs_SDlX2_hRiGnuWWgIeVga5y01rNT8BzWXNNgjrlOSCKtGQ";
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
                playSong(playButton1);

            }
        });

        // Repeat for playButton2 and playButton3
    }



    private void fetchAccessTokenFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DocumentReference docRef = db.collection("users").document(userId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot.contains("accessToken")) {
                    accessToken = documentSnapshot.getString("accessToken");
                    if (accessToken != null) {
                        Log.d("AccessToken", "Access Token Retrieved: " + accessToken);
                        // Now you can use this access token to make requests to Spotify
                    } else {
                        Toast.makeText(wrapped.this, "Access Token not found in Firestore", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(wrapped.this, "Firestore document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(wrapped.this, "Error fetching from Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Firestore", "Error fetching accessToken", e);
            }
        });
    }


    private void playSong(Button playButton) {
        //fetchAccessTokenFromFirebase();
        Log.d("sdf", accessToken);
        // playButton1.setTag("spotify:track:2eqLgrPkcSTgCvxEtsymiz");
        String songUri = "spotify:track:2eqLgrPkcSTgCvxEtsymiz"; // Make sure the button's tag is set with the correct URI
        if (songUri == null || songUri.isEmpty()) {
            Toast.makeText(this, "Song URI not available", Toast.LENGTH_SHORT).show();
            return;
        }
        // Replace with your song URI

        OkHttpClient client = new OkHttpClient(); // Assuming you're using OkHttp

        MediaType jsonMediaType = MediaType.parse("application/json");

// Don't hardcode the access token, retrieve it dynamically
        String accessToken = "BQD82F_XVPw1MvrlHE8yr0JSA0yEle9mqCjpTSiat1FlTBoxDcU8UQGMyIe3-aqJvyUtZlE435Ge6dkUdGpux6mk0kCb2z3qrt-TUQ9czlPxF9rSblpmDoC_570htawzKDV9NZiVlYc3W5-gTreAHgEey3SC-uIs_SDlX2_hRiGnuWWgIeVga5y01rNT8BzWXNNgjrlOSCKtGQ"; // Implement a method to get your access token

        RequestBody requestBody = RequestBody.create(jsonMediaType, "{\"uris\":[\"" + songUri + "\"]}");

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/players/play")
                .addHeader("Authorization", "Bearer " + accessToken)
                .post(requestBody)
                .build();


    }






    // Update the TextViews with the song names when you fetch the top tracks
    private void updateSongNames() {
        // Set the text of songName1, songName2, and songName3 to the names of the top tracks
    }
}
