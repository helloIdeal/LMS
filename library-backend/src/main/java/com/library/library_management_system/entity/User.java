// src/main/java/com/library/library_management_system/entity/User.java
package com.library.library_management_system.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String phone;
    private String address;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type")
    private MembershipType membershipType;
    
    @Column(name = "membership_start_date")
    private LocalDateTime membershipStartDate;
    
    @Column(name = "membership_end_date")
    private LocalDateTime membershipEndDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // One user can have many borrow transactions
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BorrowTransaction> borrowTransactions;
    
    // One user can have many reservations
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reservation> reservations;
    
    // Enums
    public enum Role {
        ADMIN, MEMBER
    }
    
    public enum MembershipType {
        STANDARD, PREMIUM, STUDENT
    }
    
    // Constructors
    public User() {}
    
    public User(String username, String password, String fullName, String email, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public MembershipType getMembershipType() {
        return membershipType;
    }
    
    public void setMembershipType(MembershipType membershipType) {
        this.membershipType = membershipType;
    }
    
    public LocalDateTime getMembershipStartDate() {
        return membershipStartDate;
    }
    
    public void setMembershipStartDate(LocalDateTime membershipStartDate) {
        this.membershipStartDate = membershipStartDate;
    }
    
    public LocalDateTime getMembershipEndDate() {
        return membershipEndDate;
    }
    
    public void setMembershipEndDate(LocalDateTime membershipEndDate) {
        this.membershipEndDate = membershipEndDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<BorrowTransaction> getBorrowTransactions() {
        return borrowTransactions;
    }
    
    public void setBorrowTransactions(List<BorrowTransaction> borrowTransactions) {
        this.borrowTransactions = borrowTransactions;
    }
    
    public List<Reservation> getReservations() {
        return reservations;
    }
    
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}