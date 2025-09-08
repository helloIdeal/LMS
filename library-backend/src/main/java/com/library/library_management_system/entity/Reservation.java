package com.library.library_management_system.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many reservations can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Many reservations can belong to one book
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;
    
    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;
    
    @Column(name = "notification_sent")
    private Boolean notificationSent = false;
    
    @Column(name = "notification_date")
    private LocalDateTime notificationDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;
    
    @Column(name = "queue_position")
    private Integer queuePosition;
    
    @Column(length = 500)
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enum for reservation status
    public enum ReservationStatus {
        ACTIVE,     // Reservation is active and waiting
        AVAILABLE,  // Book is now available for pickup
        FULFILLED,  // User has picked up the book
        EXPIRED,    // Reservation expired
        CANCELLED   // User cancelled the reservation
    }
    
    // Constructors
    public Reservation() {}
    
    public Reservation(User user, Book book, LocalDate reservationDate) {
        this.user = user;
        this.book = book;
        this.reservationDate = reservationDate;
        this.expiryDate = reservationDate.plusDays(7); // 7 days to pick up
        this.status = ReservationStatus.ACTIVE;
        this.notificationSent = false;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Book getBook() {
        return book;
    }
    
    public void setBook(Book book) {
        this.book = book;
    }
    
    public LocalDate getReservationDate() {
        return reservationDate;
    }
    
    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public Boolean getNotificationSent() {
        return notificationSent;
    }
    
    public void setNotificationSent(Boolean notificationSent) {
        this.notificationSent = notificationSent;
    }
    
    public LocalDateTime getNotificationDate() {
        return notificationDate;
    }
    
    public void setNotificationDate(LocalDateTime notificationDate) {
        this.notificationDate = notificationDate;
    }
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public Integer getQueuePosition() {
        return queuePosition;
    }
    
    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    // Business logic methods
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate) && 
               (status == ReservationStatus.ACTIVE || status == ReservationStatus.AVAILABLE);
    }
    
    public boolean isActive() {
        return status == ReservationStatus.ACTIVE && !isExpired();
    }
    
    public boolean isAvailableForPickup() {
        return status == ReservationStatus.AVAILABLE && !isExpired();
    }
    
    public void markAsAvailable() {
        if (status == ReservationStatus.ACTIVE) {
            this.status = ReservationStatus.AVAILABLE;
            this.expiryDate = LocalDate.now().plusDays(3); // 3 days to pick up once available
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void markAsFulfilled() {
        this.status = ReservationStatus.FULFILLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void expire() {
        if (isExpired()) {
            this.status = ReservationStatus.EXPIRED;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public void sendNotification() {
        this.notificationSent = true;
        this.notificationDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean needsNotification() {
        return status == ReservationStatus.AVAILABLE && !notificationSent;
    }
    
    public int getDaysUntilExpiry() {
        if (isExpired()) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (notificationSent == null) {
            notificationSent = false;
        }
        if (expiryDate == null && reservationDate != null) {
            expiryDate = reservationDate.plusDays(7);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // Auto-expire if past expiry date
        if (isExpired() && (status == ReservationStatus.ACTIVE || status == ReservationStatus.AVAILABLE)) {
            status = ReservationStatus.EXPIRED;
        }
    }
}