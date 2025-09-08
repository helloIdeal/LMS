package com.library.library_management_system.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "borrow_transactions")
public class BorrowTransaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Many transactions can belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Many transactions can belong to one book
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;
    
    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;
    
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    
    @Column(name = "return_date")
    private LocalDate returnDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
    
    @Column(name = "renewal_count")
    private Integer renewalCount = 0;
    
    @Column(name = "max_renewals")
    private Integer maxRenewals = 2; // Default max 2 renewals
    
    @Column(name = "fine_amount", precision = 10, scale = 2)
    private BigDecimal fineAmount = BigDecimal.ZERO;
    
    @Column(name = "fine_paid")
    private Boolean finePaid = false;
    
    @Column(name = "daily_fine_rate", precision = 5, scale = 2)
    private BigDecimal dailyFineRate = new BigDecimal("0.50"); // $0.50 per day
    
    @Column(name = "max_fine_amount", precision = 10, scale = 2)
    private BigDecimal maxFineAmount = new BigDecimal("20.00"); // Max $20 fine
    
    @Column(length = 500)
    private String notes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enum for transaction status
    public enum TransactionStatus {
        BORROWED, RETURNED, OVERDUE, RENEWED
    }
    
    // Constructors
    public BorrowTransaction() {}
    
    public BorrowTransaction(User user, Book book, LocalDate borrowDate, LocalDate dueDate) {
        this.user = user;
        this.book = book;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = TransactionStatus.BORROWED;
        this.renewalCount = 0;
        this.fineAmount = BigDecimal.ZERO;
        this.finePaid = false;
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
    
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    
    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }
    
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public TransactionStatus getStatus() {
        return status;
    }
    
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
    public Integer getRenewalCount() {
        return renewalCount;
    }
    
    public void setRenewalCount(Integer renewalCount) {
        this.renewalCount = renewalCount;
    }
    
    public Integer getMaxRenewals() {
        return maxRenewals;
    }
    
    public void setMaxRenewals(Integer maxRenewals) {
        this.maxRenewals = maxRenewals;
    }
    
    public BigDecimal getFineAmount() {
        return fineAmount;
    }
    
    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }
    
    public Boolean getFinePaid() {
        return finePaid;
    }
    
    public void setFinePaid(Boolean finePaid) {
        this.finePaid = finePaid;
    }
    
    public BigDecimal getDailyFineRate() {
        return dailyFineRate;
    }
    
    public void setDailyFineRate(BigDecimal dailyFineRate) {
        this.dailyFineRate = dailyFineRate;
    }
    
    public BigDecimal getMaxFineAmount() {
        return maxFineAmount;
    }
    
    public void setMaxFineAmount(BigDecimal maxFineAmount) {
        this.maxFineAmount = maxFineAmount;
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
    public boolean isOverdue() {
        return LocalDate.now().isAfter(dueDate) && returnDate == null;
    }
    
    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    public boolean canRenew() {
        return renewalCount < maxRenewals && !isOverdue() && returnDate == null;
    }
    
    public void renewTransaction() {
        if (canRenew()) {
            this.renewalCount++;
            this.dueDate = this.dueDate.plusDays(14); // Extend by 14 days
            this.status = TransactionStatus.RENEWED;
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    public BigDecimal calculateFine() {
        if (!isOverdue()) {
            return BigDecimal.ZERO;
        }
        
        long daysOverdue = getDaysOverdue();
        BigDecimal calculatedFine = dailyFineRate.multiply(BigDecimal.valueOf(daysOverdue));
        
        // Cap the fine at maximum amount
        if (calculatedFine.compareTo(maxFineAmount) > 0) {
            calculatedFine = maxFineAmount;
        }
        
        this.fineAmount = calculatedFine;
        return calculatedFine;
    }
    
    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.status = TransactionStatus.RETURNED;
        this.updatedAt = LocalDateTime.now();
        
        // Calculate final fine if overdue
        if (isOverdue()) {
            calculateFine();
        }
    }
    
    public void markOverdue() {
        if (isOverdue() && status != TransactionStatus.RETURNED) {
            this.status = TransactionStatus.OVERDUE;
            calculateFine();
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (renewalCount == null) {
            renewalCount = 0;
        }
        if (fineAmount == null) {
            fineAmount = BigDecimal.ZERO;
        }
        if (finePaid == null) {
            finePaid = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        
        // Auto-update status if overdue
        if (isOverdue() && status == TransactionStatus.BORROWED) {
            status = TransactionStatus.OVERDUE;
            calculateFine();
        }
    }
}