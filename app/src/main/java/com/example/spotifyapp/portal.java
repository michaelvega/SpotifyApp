package com.example.spotifyapp;

import com.google.android.material.navigation.NavigationView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import com.google.android.material.navigation.NavigationView;


import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


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
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // For demonstration purposes. Replace with actual authentication logic.
                if (!username.isEmpty() && !password.isEmpty()) {
                    Log.d("username", username);
                    Log.d("password", password);
                }
            }
        });
    }
}