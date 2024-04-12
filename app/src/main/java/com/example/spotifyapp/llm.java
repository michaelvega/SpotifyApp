package com.example.spotifyapp;

import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class llm extends BaseActivity {

    private TextView llmOutput;

    private Button submitButton;

    private static String topTracksJsonString = "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.llm);

        initializeDrawer();

        llmOutput = findViewById(R.id.llmOutput);
        submitButton = findViewById(R.id.submit);

        if (login.mAuth.getCurrentUser() != null) {
            this.fetchTopTracksJsonStringFromFirebase();
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    login.mAuth.getCurrentUser();
                    OpenAiTask openAiTask = new OpenAiTask();
                    Thread thread = new Thread(openAiTask);
                    thread.start();
                    thread.join();
                    String value = openAiTask.getValue();
                    llmOutput.setText(value);
                    Log.d("hello", value);
                    Log.d("hello2", topTracksJsonString);
                } catch (Exception e) {
                    Toast.makeText(llm.this, "bad", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public static class OpenAiTask implements Runnable {
        private String output = "";

        @Override
        public void run() {
            String systemPrompt = "You are a helpful assistant whose purpose is to predict the way a user thinks, acts, and dresses based on their music tastes. Format the response in an engaging and terse format, and write concisely. Do not be generic.";
            String userPrompt = "Here is my songs data: " + topTracksJsonString + "Given that data, please dynamically describe the way you think I act, think, and dress based on my music taste?";
            ArrayList<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", systemPrompt));
            messages.add(new ChatMessage("user", userPrompt));
            //put key
            OpenAiService service = new OpenAiService("");
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .messages(messages)
                    .model("gpt-4-turbo-preview")
                    .maxTokens(4096)
                    .build();
            List<ChatCompletionChoice> choices = service.createChatCompletion(completionRequest).getChoices();
            for (ChatCompletionChoice choice : choices) {
                output += choice.getMessage().getContent();
            }
        }

        public String getValue() {
            return output;
        }
    }

    private void fetchTopTracksJsonStringFromFirebase() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String userId = currentUser.getUid();
        DocumentReference yearlyTopTracksRef = db.collection("users").document(userId).collection("yearlyTopTracks").document("pastYear");
        yearlyTopTracksRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String topTracksData = documentSnapshot.getString("topThreeTracks");
                    if (topTracksData != null) {
                        try {
                            JSONArray topTracksArray = new JSONArray(topTracksData);
                            StringBuilder toastTextBuilder = new StringBuilder();
                            for (int i = 0; i < topTracksArray.length(); i++) {
                                JSONObject trackObj = topTracksArray.getJSONObject(i);
                                String name = trackObj.getString("name");
                                toastTextBuilder.append("Name: ").append(name).append("\n\n");
                            }
                            topTracksJsonString = toastTextBuilder.toString();
                        } catch (JSONException e) {
                            Log.e("JSON", "Failed to parse top tracks data", e);
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firestore", "Error getting documents: ", e);
            }
        });
    }
}
