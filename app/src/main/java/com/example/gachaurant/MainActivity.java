package com.example.gachaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    LinearLayout signUp;
    EditText email,password;
    Button loginButton;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    //Preset code (onCreate)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initialize Var
        email = findViewById(R.id.emailEdit);
        password = findViewById(R.id.passwordEditLogin);
        signUp = findViewById(R.id.signUpLayout);
        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        loginButton = findViewById(R.id.loginButton);

        //If user hasn't logged out lead user to the main page
        if(fAuth.getCurrentUser() != null){
            Toast.makeText(MainActivity.this,"User hasn't logged out",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            finish();
        }

        //Login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailString = email.getText().toString().trim();
                String passwordString = password.getText().toString().trim();
                //Validate user's information
                if(TextUtils.isEmpty(emailString)){
                    email.setError("Email is required");
                    return;
                }if(TextUtils.isEmpty(passwordString)){
                    email.setError("Password is required");
                    return;
                }
                if(password.length() < 6){
                    password.setError("Password's length must be >= 6 characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                fAuth.signInWithEmailAndPassword(emailString,passwordString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "User Logged In", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
                        }else{
                            Toast.makeText(MainActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        signUp.setOnClickListener(v -> goToSignUp(v)); //Go to sign up
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