package com.example.quizapp.service;

import com.example.quizapp.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class QuizUserDetailsService implements UserDetailsService {

    // In-memory data structure to store users, using username as the key
    private final Map<String, User> users = new HashMap<>();

    /**
     * Registers a new user and adds them to the in-memory collection.
     */
    public void registerUser(String username, String password, String email, String role) {
        User newUser = new User(username, email, password, role);
        users.put(username, newUser);
    }

    /**
     * Loads user details for Spring Security authentication.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = users.get(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Convert the custom User model to Spring Security's UserDetails object
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}