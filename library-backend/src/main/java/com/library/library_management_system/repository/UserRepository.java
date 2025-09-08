// UserRepository.java
package com.library.library_management_system.repository;

import com.library.library_management_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by username
    Optional<User> findByUsername(String username);
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Check if username exists
    boolean existsByUsername(String username);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Find users by role
    List<User> findByRole(User.Role role);
    
    // Find users by membership type
    List<User> findByMembershipType(User.MembershipType membershipType);
    
    // Search users by name or username
    @Query("SELECT u FROM User u WHERE u.fullName LIKE %:searchTerm% OR u.username LIKE %:searchTerm%")
    List<User> findByFullNameContainingOrUsernameContaining(@Param("searchTerm") String searchTerm);
    
    // Find members only (exclude admins)
    @Query("SELECT u FROM User u WHERE u.role = 'MEMBER'")
    List<User> findAllMembers();
    
    // Find users with expired memberships
    @Query("SELECT u FROM User u WHERE u.membershipEndDate < CURRENT_DATE AND u.role = 'MEMBER'")
    List<User> findUsersWithExpiredMembership();
}