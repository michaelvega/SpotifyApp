package com.example.spotifyapp;


import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class llm extends BaseActivity {

    private EditText inputEditText;
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
                    Toast.makeText(llm.this, inputText, Toast.LENGTH_LONG).show();
                } else {
                    // Inform the user that the input is empty
                    Toast.makeText(llm.this, "Please enter some text!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}