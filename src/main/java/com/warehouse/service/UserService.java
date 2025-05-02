package com.warehouse.service;

import com.warehouse.exception.ResourceNotFoundException;
import com.warehouse.model.User;
import com.warehouse.repository.UserRepository;
import com.warehouse.payload.request.UserUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Map<String, String> passwordResetTokens = new HashMap<>();

    public User updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());

        return userRepository.save(user);
    }

    public void createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        String token = UUID.randomUUID().toString();
        passwordResetTokens.put(token, email);

        System.out.println("✅ Password reset token for " + email + ": " + token);
    }

    public void resetPassword(String token, String newPassword) {
        String email = passwordResetTokens.get(token);
        if (email == null) {
            throw new ResourceNotFoundException("Invalid or expired password reset token.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokens.remove(token);
    }
}
