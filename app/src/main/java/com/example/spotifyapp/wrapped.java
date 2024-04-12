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
                playSong(0); // Assuming 0 is the index of the first song in your list
            }
        });

        // Repeat for playButton2 and playButton3
    }

    private void playSong(int songIndex) {
        // Use the Spotify Web API to play the song at the given index
        // You will need the song's URI, which you can get from the top tracks JSON
        // Example: "spotify:track:3n3Ppam7vgaVa1iaRUc9Lp"
        String songUri = "spotify:track:..."; // Replace with the actual URI

        Request request = new Request.Builder()
                .url("https://api.spotify.com/v1/me/top/tracks?limit=3")
                .addHeader("Authorization", "Bearer " + getmAccessToken())
                .build();

        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                if (songIndex < itemsArray.length()) {
                    JSONObject trackObject = itemsArray.getJSONObject(songIndex);
                    songUri = trackObject.getString("uri");

                    // Play the song with the fetched URI
                    Request playRequest = new Request.Builder()
                            .url("https://api.spotify.com/v1/me/player/play")
                            .addHeader("Authorization", "Bearer " + getmAccessToken())
                            .post(RequestBody.create("{\"uris\": [\"" + songUri + "\"]}", MediaType.parse("application/json")))
                            .build();

                    mOkHttpClient.newCall(playRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            // Handle failure
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            // Handle response
                        }
                    });
                } else {
                    Toast.makeText(this, "Song index out of range", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to fetch top tracks", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException | JSONException e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Execute the request to play the song
        // Note: You need a Spotify Premium account to use the play endpoint
    }

    // Update the TextViews with the song names when you fetch the top tracks
    private void updateSongNames() {
        // Set the text of songName1, songName2, and songName3 to the names of the top tracks
    }
}
