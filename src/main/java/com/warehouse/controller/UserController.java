package com.warehouse.controller;

import com.warehouse.model.User;
import com.warehouse.payload.request.UserUpdateRequest;
import com.warehouse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<String> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {

        User updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok("✅ User updated successfully: " + updatedUser.getEmail());
    }
}
