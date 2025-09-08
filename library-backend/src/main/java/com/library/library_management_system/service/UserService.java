package com.library.library_management_system.service;

import com.library.library_management_system.entity.User;
import com.library.library_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Create new user
    public User createUser(User user) {
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set membership dates for members
        if (user.getRole() == User.Role.MEMBER) {
            user.setMembershipStartDate(LocalDateTime.now());
            user.setMembershipEndDate(LocalDateTime.now().plusYears(1)); // 1 year membership
        }
        
        return userRepository.save(user);
    }
    
    // Authenticate user
    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }
    
    // Find user by username
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Find user by ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    // Get all members (excluding admins)
    public List<User> getAllMembers() {
        return userRepository.findAllMembers();
    }
    
    // Search users
    public List<User> searchUsers(String searchTerm) {
        return userRepository.findByFullNameContainingOrUsernameContaining(searchTerm);
    }
    
    // Update user
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        user.setMembershipType(userDetails.getMembershipType());
        
        return userRepository.save(user);
    }
    
    // Delete user
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
    
    // Check if user membership is valid
    public boolean isMembershipValid(User user) {
        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }
        return user.getMembershipEndDate() != null && 
               user.getMembershipEndDate().isAfter(LocalDateTime.now());
    }
    
    // Extend membership
    public User extendMembership(Long userId, int months) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getMembershipEndDate() != null) {
            user.setMembershipEndDate(user.getMembershipEndDate().plusMonths(months));
        } else {
            user.setMembershipEndDate(LocalDateTime.now().plusMonths(months));
        }
        
        return userRepository.save(user);
    }
    
    // Get users with expired memberships
    public List<User> getUsersWithExpiredMembership() {
        return userRepository.findUsersWithExpiredMembership();
    }
}