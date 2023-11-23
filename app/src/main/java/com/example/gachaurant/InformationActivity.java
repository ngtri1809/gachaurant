package com.example.gachaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class InformationActivity extends AppCompatActivity {
    private static final String TAG = "InformationActivity";
    Map<String, Boolean> preference;
    Button confirmButton;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        //Intiatilize variables
        confirmButton = findViewById(R.id.confirmButton);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        preference = new HashMap<>();
        progressBar = findViewById(R.id.progressBar2);
        //Create an array of all check box
        int[] checkBoxIds = {
                R.id.vietnameseCB,
                R.id.chineseCB,
                R.id.koreanCB,
                R.id.thaiCB,
                R.id.mexicanCB,
                R.id.japaneseCB,
                R.id.indianCB,
                R.id.ramenCB,
                R.id.chickenCB,
                R.id.bbqCB,
                R.id.fastFoodCB,
                R.id.italianCB
        };
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Loop through the array to check for the checked box
                for (int checkBoxId : checkBoxIds) {
                    CheckBox checkBox = findViewById(checkBoxId);
                    Log.d("Checkbox State", checkBox.getText().toString() + ": " + checkBox.isChecked());
                    preference.put(checkBox.getText().toString(), checkBox.isChecked());
                }
                userID = fAuth.getCurrentUser().getUid();

                DocumentReference documentReference = fStore.collection("users").document(userID);
                //Create map of user's info
                Map<String, Object> pref = new HashMap<>();
                pref.put("preference", preference);
                documentReference.update(pref).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d(TAG, "onSuccess: user profile is created for" + userID + "and preference" + preference);
                    }
                });
                Toast.makeText(InformationActivity.this, "User Logged In", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainPageActivity.class));
            }
        });
    }
}