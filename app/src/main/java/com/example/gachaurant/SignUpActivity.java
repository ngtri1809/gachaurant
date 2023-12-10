package com.example.gachaurant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gachaurant.helpers.StringHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    EditText full_name, email, password, confirm, user_name;
    Button signUpBtn;
    LinearLayout signIn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initialize var
        full_name = findViewById(R.id.fullnameEdit);
        user_name = findViewById(R.id.userNameEdit);
        email = findViewById(R.id.emailEdit);
        password = findViewById(R.id.passwordEdit);
        confirm = findViewById(R.id.confirmEdit);
        signUpBtn = findViewById(R.id.loginButton);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);
        signIn = findViewById(R.id.signInLayout);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processFormFields();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    //Process form view method
    public void processFormFields(){
        String emailString = email.getText().toString().trim();
        String passwordString = password.getText().toString().trim();
        String fullNameString = full_name.getText().toString().trim();
        String userNameString = user_name.getText().toString().trim();

        //Validate user's information
        if(!validateFullName(fullNameString) || !validateUserName(userNameString)|| !validateEmail(emailString) || !validatePassword(passwordString)){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        fAuth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(SignUpActivity.this, "User Created successfully", Toast.LENGTH_SHORT).show();
                userID = fAuth.getCurrentUser().getUid();
                DocumentReference documentReference = fStore.collection("users").document(userID);
                //Create map of user's info
                Map<String, Object> user = new HashMap<>();
                user.put("fullName", fullNameString);
                user.put("email", emailString);
                user.put("userName", userNameString);
                user.put("location", null);
                user.put("distance", null);
                user.put("preference", null);
                user.put("restaurantList", null);
                user.put("restaurantInventory", null);
                user.put("level", 1);
                user.put("experience", 0);
                //Insert user map into database
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: user profile is created for" + userID);
                    }
                });
                startActivity(new Intent(getApplicationContext(), InformationActivity.class)); //After successfully registered, sent users to information page
            }else{
                Toast.makeText(SignUpActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public boolean validateFullName(String fullName){
        //Check if first name is empty
        if(fullName.isEmpty()){
            full_name.setError("Full name cannot be empty");
            return false;
        }else{
            full_name.setError(null);
            return true;
        }
    }
    public boolean validateUserName(String userName){
        //Check if username is empty
        if(userName.isEmpty()){
            user_name.setError("username cannot be empty");
            return false;
        }else{
            user_name.setError(null);
            return true;
        }
    }
    public boolean validateEmail(String emailString){
        //Check if email is empty
        if(emailString.isEmpty()){
            email.setError("Email cannot be empty");
            return false;
        }else if(!StringHelper.regexEmailValidationPattern(emailString)){ //Check if email is valid
            email.setError("Please enter a valid email");
            return false;
        }
        else{
            email.setError(null);
            return true;
        }
    }
    public boolean validatePassword(String passwordString){
        String confirmString = confirm.getText().toString();
        //Check if first name is empty
        if(passwordString.isEmpty() || confirmString.isEmpty()) {
            password.setError("Password cannot be empty");
            confirm.setError("Confirm password can not be empty");
            return false;
        } else if(passwordString.length() < 6){
            password.setError("Password length must be >= 6 characters");
            return false;
        }else if(!passwordString.equals(confirmString)){
            confirm.setError("Passwords must be the same");
            return false;
        }else{
            password.setError(null);
            confirm.setError(null);
            return true;
        }
    }
}
