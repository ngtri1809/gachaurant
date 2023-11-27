package com.example.gachaurant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    EditText fullName,email,userName,location;
    Button backButton,doneButton;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    FirebaseUser user;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        //Init
        fullName = findViewById(R.id.fullnameDB);
        email = findViewById(R.id.emailDB);
        userName = findViewById(R.id.userNameDB);
        location = findViewById(R.id.locationDB);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        backButton = findViewById(R.id.backButton);
        doneButton = findViewById(R.id.doneButton);
        user = fAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar3);
        //Fetching data from Firebase
        DocumentReference docRef = fStore.collection("users").document(userId);
        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                fullName.setText(value.getString("fullName"));
                userName.setText(value.getString("userName"));
                email.setText(value.getString("email"));
                location.setText(value.getString("location"));
            }
        });

        //After clicking the done button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fullName.getText().toString().isEmpty() || userName.getText().toString().isEmpty() || email.getText().toString().isEmpty()) {
                    Toast.makeText(UserProfileActivity.this, "One or Many Fields are Empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                    user.updateEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Map<String, Object> edited = new HashMap<>();
                            edited.put("fullName", fullName.getText().toString());
                            edited.put("email", email.getText().toString());
                            edited.put("userName", userName.getText().toString());
                            docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                                }
                            });
                            Toast.makeText(UserProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfileActivity.this, "email is not updated", Toast.LENGTH_SHORT).show();
                        }
                    });

            }
        });
        //Back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private boolean isEditTextEdited(EditText editText, String originalValue) {
        return !editText.getText().toString().equals(originalValue);
    }

}