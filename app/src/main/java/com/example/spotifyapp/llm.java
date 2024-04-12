package com.example.spotifyapp;


import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.api.SystemParameterOrBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import java.util.List;

import okhttp3.OkHttpClient;


public class llm extends BaseActivity {

    private EditText inputEditText;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private Button submitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.llm);

        initializeDrawer();

        inputEditText = findViewById(R.id.input);
        submitButton = findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the text from the EditText
                String inputText = inputEditText.getText().toString();

                // Check if the input text is not empty
                if(!inputText.isEmpty()){
                    // Show the text in a Toast message
                    try {
                        OpenAiTask openAiTask = new OpenAiTask();
                        Thread thread = new Thread(openAiTask);
                        thread.start();
                        thread.join();
                        String value = openAiTask.getValue();
                        Toast.makeText(llm.this, value, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(llm.this, "bad", Toast.LENGTH_LONG).show();

                    }
                } else {
                    // Inform the user that the input is empty
                    Toast.makeText(llm.this, "Please enter some text!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private class OpenAiTask implements Runnable {
        private String output = "";

        @Override
        public void run() {
            // put key
            OpenAiService service = new OpenAiService("");
            CompletionRequest completionRequest = CompletionRequest.builder()
                    .prompt("You are a helpful assistant whose purpose is to predict the way a user thinks, acts, and dresses based on their music tastes. You must format your response in JSON. Given that I enjoy listening to {songs}, please dynamically describe the way you think I act, think, and dress based on my music taste?")
                    .model("gpt-3.5-turbo-instruct")
                    .echo(true)
                    .build();
            List<CompletionChoice> choices = service.createCompletion(completionRequest).getChoices();
            for (CompletionChoice choice : choices) {
                output += choice.getText();
            }
        }

        public String getValue() {
            return output;
        }
    }
}