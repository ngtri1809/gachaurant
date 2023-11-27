package com.example.gachaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class InformationActivity extends AppCompatActivity {
    private static final String TAG = "InformationActivity";
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 100;
    Map<String, Boolean> preference;
    Button confirmButton;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    ProgressBar progressBar;
    EditText locationEt;
    ImageView locationImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        //Initialize variables
        confirmButton = findViewById(R.id.confirmButton);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        preference = new HashMap<>();
        progressBar = findViewById(R.id.progressBar2);
        locationEt = findViewById(R.id.locationEditText);
        locationImg = findViewById(R.id.locationImage);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
        //When click on location
        locationImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });
        //Confirm button click
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
                Map<String, Object> info = new HashMap<>();
                info.put("preference", preference);
                info.put("location", locationEt.getText().toString().trim());
                documentReference.update(info).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void getLastLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        Geocoder geocoder = new Geocoder(InformationActivity.this, Locale.getDefault());
                        List<Address> address;
                        try {
                            address = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1); //Get address of the user
                            locationEt.setText(address.get(0).getAddressLine(0)); //+ ", " + address.get(0).getLocality()
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });
        }else {
            askPermission();
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(InformationActivity.this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults.length >0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Toast.makeText(this, "Permission denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

}