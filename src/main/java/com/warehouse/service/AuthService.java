package com.warehouse.service;

import com.warehouse.model.User;
import com.warehouse.model.Role;
import com.warehouse.model.ERole;
import com.warehouse.payload.request.SignupRequest;
import com.warehouse.repository.RoleRepository;
import com.warehouse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public void register(SignupRequest signupRequest) {
        String plainPassword = signupRequest.getPassword();

        User user = new User();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(plainPassword);
        user.setPhoneNumber(signupRequest.getPhoneNumber());

        Set<Role> roles = new HashSet<>();
        for (String roleName : signupRequest.getRoles()) {
            ERole roleEnum = ERole.valueOf(roleName.toUpperCase());
            Role role = roleRepository.findByName(roleEnum)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + roleEnum));
            roles.add(role);
        }

        user.setRoles(roles);

        userRepository.save(user);
    }
}
