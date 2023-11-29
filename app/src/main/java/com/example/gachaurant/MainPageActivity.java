package com.example.gachaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gachaurant.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainPageActivity extends AppCompatActivity {
    Button logOut;
    FirebaseAuth fAuth;
    //ImageButton profileButton;

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //TODO figure out why binding.bottomNavigationView doesn't work, expects logOut button
        // functionality
        binding.bottomNavigationView.setOnItemSelectedListener(item ->{

        });

        //setContentView(R.layout.activity_main_page);
        //Initialize
        logOut = findViewById(R.id.logOutButton);
        fAuth = FirebaseAuth.getInstance();
       //profileButton = findViewById(R.id.profileButton);

       /** profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),UserProfileActivity.class));
            }
        });
        **/
        //Go to User Profile
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Toast.makeText(MainPageActivity.this, "User Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }
}