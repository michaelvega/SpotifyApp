package com.example.spotifyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class account extends BaseActivity {

    private Button deleteAccountBtn;
    private Button updateAccountBtn;

    private EditText newEmailInput;

    private Button signOutButton;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
        initializeDrawer();

        newEmailInput = findViewById(R.id.new_email_input);

        deleteAccountBtn = findViewById(R.id.delete_account_btn);

        signOutButton = findViewById(R.id.signout_button);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(account.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
                startActivity(intent);
            }
        });
        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });

        // Update account button setup
        updateAccountBtn = findViewById(R.id.update_account_btn);
        updateAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAccountEmail();
            }
        });
    }

    private void updateAccountEmail() {

        if (mAuth.getCurrentUser() != null) {
            // Get the email entered by the user
            String newEmail = newEmailInput.getText().toString().trim();

            if (!newEmail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                String userId = mAuth.getCurrentUser().getUid();

                // Update the "spotifyEmail" field in the Firestore document
                db.collection("users").document(userId)
                        .update("spotifyEmail", newEmail)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(account.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(account.this, "Failed to update email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("Firestore", "Error updating user email", e);
                        });
            } else {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void deleteAccount() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // Delete user data from Firestore if necessary
            deleteUserDataFromFirestore(mAuth.getCurrentUser().getUid());

            // Delete the user
            mAuth.getCurrentUser().delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(account.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                    // Redirect to login or other appropriate activity
                    finish();
                } else {
                    Toast.makeText(account.this, "Failed to delete account: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteUserDataFromFirestore(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Example: delete user document from "users" collection
        db.collection("users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data deleted successfully"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error deleting user data", e));
    }
}
