package com.library.library_management_system.controller;

import com.library.library_management_system.entity.User;
import com.library.library_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Register new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            // Don't return password in response
            createdUser.setPassword(null);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Login user
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> user = userService.authenticateUser(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            if (user.isPresent()) {
                User authenticatedUser = user.get();
                // Don't return password in response
                authenticatedUser.setPassword(null);
                return ResponseEntity.ok(authenticatedUser);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Login failed: " + e.getMessage());
        }
    }
    
    // Get all members (Admin only)
    @GetMapping("/members")
    public ResponseEntity<List<User>> getAllMembers() {
        try {
            List<User> members = userService.getAllMembers();
            // Remove passwords from response
            members.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(members);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userService.findById(id);
            if (user.isPresent()) {
                User foundUser = user.get();
                foundUser.setPassword(null);
                return ResponseEntity.ok(foundUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving user: " + e.getMessage());
        }
    }
    
    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            Optional<User> user = userService.findByUsername(username);
            if (user.isPresent()) {
                User foundUser = user.get();
                foundUser.setPassword(null);
                return ResponseEntity.ok(foundUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving user: " + e.getMessage());
        }
    }
    
    // Search users
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String searchTerm) {
        try {
            List<User> users = userService.searchUsers(searchTerm);
            // Remove passwords from response
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            updatedUser.setPassword(null);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating user: " + e.getMessage());
        }
    }
    
    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting user: " + e.getMessage());
        }
    }
    
    // Check membership validity
    @GetMapping("/{id}/membership/valid")
    public ResponseEntity<?> checkMembershipValidity(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userService.findById(id);
            if (userOpt.isPresent()) {
                boolean isValid = userService.isMembershipValid(userOpt.get());
                return ResponseEntity.ok().body(new MembershipValidityResponse(isValid));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error checking membership: " + e.getMessage());
        }
    }
    
    // Extend membership
    @PostMapping("/{id}/membership/extend")
    public ResponseEntity<?> extendMembership(@PathVariable Long id, @RequestBody ExtendMembershipRequest request) {
        try {
            User updatedUser = userService.extendMembership(id, request.getMonths());
            updatedUser.setPassword(null);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error extending membership: " + e.getMessage());
        }
    }
    
    // Get users with expired memberships
    @GetMapping("/expired-memberships")
    public ResponseEntity<List<User>> getUsersWithExpiredMembership() {
        try {
            List<User> users = userService.getUsersWithExpiredMembership();
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Inner classes for request/response DTOs
    public static class LoginRequest {
        private String username;
        private String password;
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class MembershipValidityResponse {
        private boolean valid;
        
        public MembershipValidityResponse(boolean valid) {
            this.valid = valid;
        }
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
    }
    
    public static class ExtendMembershipRequest {
        private int months;
        
        public int getMonths() { return months; }
        public void setMonths(int months) { this.months = months; }
    }
}