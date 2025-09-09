package com.library.library_management_system.service;

import com.library.library_management_system.entity.User;
import com.library.library_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        if (user.getRole() == User.Role.MEMBER) {
            user.setMembershipStartDate(LocalDateTime.now());
            user.setMembershipEndDate(LocalDateTime.now().plusYears(1));
        }
        
        return userRepository.save(user);
    }
    
    public Optional<User> authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && password.equals(user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public List<User> getAllMembers() {
        return userRepository.findAllMembers();
    }
    
    public List<User> searchUsers(String searchTerm) {
        return userRepository.findByFullNameContainingOrUsernameContaining(searchTerm);
    }
    
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
    
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
    
    public boolean isMembershipValid(User user) {
        if (user.getRole() == User.Role.ADMIN) {
            return true;
        }
        return user.getMembershipEndDate() != null && 
               user.getMembershipEndDate().isAfter(LocalDateTime.now());
    }
    
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
    
    public List<User> getUsersWithExpiredMembership() {
        return userRepository.findUsersWithExpiredMembership();
    }
}