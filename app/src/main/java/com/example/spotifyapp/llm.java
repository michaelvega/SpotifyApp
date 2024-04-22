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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class llm extends BaseActivity {

    private FirebaseAuth mAuth;

    private TextView llmOutput;

    private TextView loading;

    private Button submitButton;

    private static String topTracksJsonString = "";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.llm);
        mAuth = FirebaseAuth.getInstance();

        initializeDrawer();

        llmOutput = findViewById(R.id.llmOutput);
        submitButton = findViewById(R.id.submit);
        loading = findViewById(R.id.loading);

        if (mAuth.getCurrentUser() != null) {
            this.fetchTopTracksJsonStringFromFirebase();
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(llm.this, "Sent the request!", Toast.LENGTH_LONG).show();


                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Future<String> future = executorService.submit(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        OpenAiTask openAiTask = new OpenAiTask();
                        openAiTask.run(); // Execute the task in this Callable
                        return openAiTask.getValue();
                    }
                });

                executorService.shutdown(); // Shutdown executor after submitting the task

                try {
                    // Wait up to 40 seconds for the task to complete and get the result
                    String value = future.get(40, TimeUnit.SECONDS);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            llmOutput.setText(value);
                            loading.setText("");
                            Log.d("hello", value);
                            Log.d("hello2", topTracksJsonString);
                        }
                    });
                } catch (TimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(llm.this, "Request timed out. Please try again.", Toast.LENGTH_LONG).show();
                            loading.setText("");
                            Log.d("llm fail:", "Timeout while waiting for the response");
                        }
                    });
                } catch (InterruptedException | ExecutionException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(llm.this, "An issue occurred.", Toast.LENGTH_LONG).show();
                            loading.setText("");
                            Log.d("llm fail:", e.toString());
                        }
                    });
                }
            }
        });

    }

    public static class OpenAiTask implements Runnable {
        private String output = "";

        @Override
        public void run() {
            String systemPrompt = "You are a helpful assistant whose purpose is to predict the way a user thinks, acts, and dresses based on their music tastes. Format the response in an engaging and terse format, and write concisely. Do not be generic. ";
            String userPrompt = "Here is my songs data: " + topTracksJsonString + "Given that data, please dynamically describe the way you think I act, think, and dress based on my music taste?";
            ArrayList<ChatMessage> messages = new ArrayList<>();
            messages.add(new ChatMessage("system", systemPrompt));
            messages.add(new ChatMessage("user", userPrompt));
            try {
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
            } catch (Exception e) {
                output = "Try again please!";
                Log.e("error", e.getMessage());
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
