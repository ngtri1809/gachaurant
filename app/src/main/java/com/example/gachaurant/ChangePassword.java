package com.example.gachaurant;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity {

    private EditText currentPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    Button saveButton;
    Button backBtn;


    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_changepw);

        fAuth = FirebaseAuth.getInstance();

        currentPasswordEditText = findViewById(R.id.editTextCurrentPassword);
        newPasswordEditText = findViewById(R.id.editTextNewPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        saveButton = findViewById(R.id.saveBtn);
        backBtn = findViewById(R.id.backBtn);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void changePassword() {
        String currentPassword = currentPasswordEditText.getText().toString();
        final String newPassword = newPasswordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();

        FirebaseUser user = fAuth.getCurrentUser();

        if (user != null && user.getEmail() != null) {
            fAuth.signInWithEmailAndPassword(user.getEmail(), currentPassword)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                // User authentication successful, change password
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ChangePassword.this,
                                                            "Password changed successfully",
                                                            Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(ChangePassword.this,
                                                            "Failed to change password",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(ChangePassword.this,
                                        "Authentication failed. Please check your current password.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Handle the case where the user is not signed in
            Toast.makeText(ChangePassword.this,
                    "User not signed in.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

