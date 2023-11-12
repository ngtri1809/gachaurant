package com.user_manager.rest_controllers;

import com.user_manager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import java.util.HashMap;
//import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RegisterApiController {

    @Autowired
    UserService userService;

    @PostMapping("/user/register")
    public ResponseEntity registerNewUser(@RequestParam("full_name") String full_name,
                                          @RequestParam("username") String username,
                                          @RequestParam("email") String email,
                                          @RequestParam("password") String password){
        if(full_name.isEmpty() || email.isEmpty() || password.isEmpty() || username.isEmpty())
            return new ResponseEntity<>("Please complete all fields", HttpStatus.BAD_REQUEST);

        //Encrypt the password
        String hashed_password = BCrypt.hashpw(password,BCrypt.gensalt());
        //Register New User
        int result = userService.registerNewUserServiceMethod(full_name, username, email, hashed_password);
        if(result != 1)
            return new ResponseEntity<>("Failed to register User", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
