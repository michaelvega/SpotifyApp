package com.example.spotifyapp;


import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class portal extends BaseActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portal);

        initializeDrawer();
        setupLoginForm();
    }

    private void setupLoginForm() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your login logic here
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // For demonstration purposes. Replace with actual authentication logic.
                if (!email.isEmpty() && !password.isEmpty()) {
                    Log.d("email", email);
                    Log.d("password", password);
                } else {
                    Toast.makeText(portal.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}