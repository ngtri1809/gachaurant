package com.example.gachaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gachaurant.helpers.StringHelper;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText full_name, email, password, confirm, user_name;
    Button signUpBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Initialize var
        full_name = findViewById(R.id.fullnameEdit);
        user_name = findViewById(R.id.usernameEdit);
        email = findViewById(R.id.emailEdit);
        password = findViewById(R.id.passwordEdit);
        confirm = findViewById(R.id.confirmEdit);
        signUpBtn = findViewById(R.id.signUpBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processFormFields();
            }
        });
    }

    public void goToHome(View view){
        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Process form view method
    public void processFormFields(){
        //Check for errors
        if(!validateFullName() || !validateUserName()|| !validateEmail() || !validatePassword()){
            return;
        }
        //1.166
        // Instantiate the RequestQueue using Volley Library
        RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);
        //The URL Posting TO:
        String url = "http://192.168.1.166:8080/api/v1/user/register";
        //String request obj: Request a string response to the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response
                        if(response.equalsIgnoreCase("success")){
                            full_name.setText(null);
                            user_name.setText(null);
                            email.setText(null);
                            password.setText(null);
                            confirm.setText(null);
                            Toast.makeText(SignUpActivity.this,"Registration Successful", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        System.out.println(error.getMessage()); // To track other errors
                        if (error instanceof AuthFailureError) {
                            // Handle authentication failure
                            Toast.makeText(SignUpActivity.this, "Authentication Failed. Please log in again.", Toast.LENGTH_LONG).show();
                            // You may also consider redirecting the user to a login screen or taking other appropriate actions.
                        }else if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            // Handle 401 error - Unauthorized
                            Toast.makeText(SignUpActivity.this, "Unauthorized. You don't have permission to access this resource.", Toast.LENGTH_LONG).show();
                            // You may provide an option for the user to log in or request new credentials.
                        }else {
                            Toast.makeText(SignUpActivity.this, "Registration Unsuccessful", Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
                    //This object post the information to the spring boot database
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("full_name", full_name.getText().toString());
                        params.put("username", user_name.getText().toString());
                        params.put("email", email.getText().toString());
                        params.put("password", password.getText().toString());
                        return params;
                    }
                };
        queue.add(stringRequest);
    }

    public boolean validateFullName(){
        String fullName = full_name.getText().toString();
        //Check if first name is empty
        if(fullName.isEmpty()){
            full_name.setError("Full name cannot be empty");
            return false;
        }else{
            full_name.setError(null);
            return true;
        }
    }
    public boolean validateUserName(){
        String userName = user_name.getText().toString();
        //Check if first name is empty
        if(userName.isEmpty()){
            user_name.setError("username cannot be empty");
            return false;
        }else{
            user_name.setError(null);
            return true;
        }
    }
    public boolean validateEmail(){
        String emailString = email.getText().toString();
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
    public boolean validatePassword(){
        String passwordString = password.getText().toString();
        String confirmString = confirm.getText().toString();
        //Check if first name is empty
        if(passwordString.isEmpty() || confirmString.isEmpty()) {
            password.setError("Password cannot be empty");
            confirm.setError("Confirm password can not be empty");
            return false;
        } else if(!passwordString.equals(confirmString)){
            confirm.setError("Passwords must be the same");
            return false;
        }else{
            password.setError(null);
            confirm.setError(null);
            return true;
        }
    }
}
