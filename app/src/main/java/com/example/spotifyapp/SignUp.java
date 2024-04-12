package com.example.spotifyapp;


import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class SignUp extends BaseActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button signUpButton;

    private Button signOutButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        mAuth = FirebaseAuth.getInstance();

        //initializeDrawer();
        setupLoginForm();
    }

    private void setupLoginForm() {
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        signUpButton = findViewById(R.id.sign_up_button);
        signOutButton = findViewById(R.id.signout_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement your login logic here
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // For demonstration purposes. Replace with actual authentication logic.
                if (!username.isEmpty() && !password.isEmpty()) {
                    Log.d("username", username);
                    Log.d("password", password);
                    signUp(username, password);
                } else {
                    Toast.makeText(SignUp.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                updateUI(null); // You might want to navigate back to the login screen here
            }
        });
    }

    private void signUp(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SignIn", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        Toast.makeText(SignUp.this, "Registration successful.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SignUp", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignUp.this, "Registration failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        // Navigate to your main activity if the user is signed in, or stay here and show an error if not
        if (user != null) {
            // Intent to start your new activity

            String userId = user.getUid();
            Toast.makeText(SignUp.this, "Your user ID is: " + userId,
                    Toast.LENGTH_SHORT).show();
            Log.d("userid", userId);
            initializeDrawer();
        } else {

            Toast.makeText(SignUp.this, "Signed Out",
                    Toast.LENGTH_SHORT).show();
            Log.w("login", "signed out");

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
}