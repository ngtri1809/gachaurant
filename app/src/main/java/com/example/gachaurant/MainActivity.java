package com.example.gachaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView signUp;
    //Preset code (onCreate)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signUp = findViewById(R.id.signUpText);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignUp(v);
            }
        });
        // Example code in an Android activity or fragment
        String serverAddress = "192.168.1.166";  // Update with your server's address
        int serverPort = 8080;  // Update with your server's port

        Log.d("NetworkDebug", "Server Address: " + serverAddress);
        Log.d("NetworkDebug", "Server Port: " + serverPort);

// Continue with your network request using serverAddress and serverPort

    }

    public void goToSignUp(View view){
        // Create an Intent to specify the source and destination activities
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        // Create an activity with the created intent
        startActivity(intent);
        //finish the activity
        finish();
    }
}