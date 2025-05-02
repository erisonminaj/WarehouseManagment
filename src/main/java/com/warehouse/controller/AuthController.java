package com.warehouse.controller;

import com.warehouse.model.ERole;
import com.warehouse.model.Role;
import com.warehouse.model.User;
import com.warehouse.payload.request.LoginRequest;
import com.warehouse.payload.request.SignupRequest;
import com.warehouse.payload.response.JwtResponse;
import com.warehouse.payload.response.MessageResponse;
import com.warehouse.repository.RoleRepository;
import com.warehouse.repository.UserRepository;
import com.warehouse.security.jwt.JwtUtils;
import com.warehouse.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setPhoneNumber(signUpRequest.getPhoneNumber());

        Set<String> strRoles = signUpRequest.getRoles(); // Now this will be an array
        Set<Role> roles = new HashSet<>();

        if (strRoles != null && !strRoles.isEmpty()) {
            strRoles.forEach(role -> {
                try {
                    ERole roleEnum = ERole.valueOf(role.toUpperCase()); // Convert to uppercase
                    Role roleEntity = roleRepository.findByName(roleEnum)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(roleEntity);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Error: Invalid role value: " + role);
                }
            });
        } else {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: No roles provided."));
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


}