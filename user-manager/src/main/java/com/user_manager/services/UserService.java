package com.user_manager.services;

import com.user_manager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    public int registerNewUserServiceMethod(String full_name, String username, String email, String password){
        return userRepository.registerNewUser(full_name, username, email, password);
    }
}
