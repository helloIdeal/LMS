package com.library.library_management_system.controller;

import com.library.library_management_system.entity.BorrowTransaction;
import com.library.library_management_system.service.BorrowTransactionService;
import com.library.library_management_system.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class BorrowTransactionController {
    
    @Autowired
    private BorrowTransactionService borrowTransactionService;
    
    @Autowired
    private ReservationService reservationService;
    
    // Borrow a book
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest request) {
        try {
            BorrowTransaction transaction = borrowTransactionService.borrowBook(
                request.getUserId(), 
                request.getBookId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error borrowing book: " + e.getMessage());
        }
    }
    
    // Return a book
    @PostMapping("/{transactionId}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long transactionId) {
        try {
            BorrowTransaction transaction = borrowTransactionService.returnBook(transactionId);
            
            // Check if there are any reservations for this book
            reservationService.processBookReturn(transaction.getBook().getId());
            
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error returning book: " + e.getMessage());
        }
    }
    
    // Renew a book
    @PostMapping("/{transactionId}/renew")
    public ResponseEntity<?> renewBook(@PathVariable Long transactionId) {
        try {
            BorrowTransaction transaction = borrowTransactionService.renewBook(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error renewing book: " + e.getMessage());
        }
    }
    
    // Get all transactions (Admin only)
    @GetMapping
    public ResponseEntity<List<BorrowTransaction>> getAllTransactions() {
        try {
            List<BorrowTransaction> transactions = borrowTransactionService.getAllTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        try {
            Optional<BorrowTransaction> transaction = borrowTransactionService.findById(id);
            if (transaction.isPresent()) {
                return ResponseEntity.ok(transaction.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving transaction: " + e.getMessage());
        }
    }
    
    // Get user's borrowing history
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<BorrowTransaction>> getUserBorrowingHistory(@PathVariable Long userId) {
        try {
            List<BorrowTransaction> transactions = borrowTransactionService.getUserBorrowingHistory(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get user's active borrowings
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<BorrowTransaction>> getUserActiveBorrowings(@PathVariable Long userId) {
        try {
            List<BorrowTransaction> transactions = borrowTransactionService.getUserActiveBorrowings(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get overdue transactions
    @GetMapping("/overdue")
    public ResponseEntity<List<BorrowTransaction>> getOverdueTransactions() {
        try {
            List<BorrowTransaction> transactions = borrowTransactionService.getOverdueTransactions();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get transactions due soon
    @GetMapping("/due-soon")
    public ResponseEntity<List<BorrowTransaction>> getTransactionsDueSoon(
            @RequestParam(defaultValue = "3") int days) {
        try {
            List<BorrowTransaction> transactions = borrowTransactionService.getTransactionsDueSoon(days);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get transactions with unpaid fines
    @GetMapping("/unpaid-fines")
    public ResponseEntity<List<BorrowTransaction>> getTransactionsWithUnpaidFines() {
        try {
            List<BorrowTransaction> transactions = borrowTransactionService.getTransactionsWithUnpaidFines();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get transactions by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BorrowTransaction>> getTransactionsByStatus(@PathVariable String status) {
        try {
            BorrowTransaction.TransactionStatus transactionStatus = 
                BorrowTransaction.TransactionStatus.valueOf(status.toUpperCase());
            List<BorrowTransaction> transactions = borrowTransactionService.getTransactionsByStatus(transactionStatus);
            return ResponseEntity.ok(transactions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get transactions by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<BorrowTransaction>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<BorrowTransaction> transactions = borrowTransactionService.getTransactionsByDateRange(startDate, endDate);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Pay fine
    @PostMapping("/{transactionId}/pay-fine")
    public ResponseEntity<?> payFine(@PathVariable Long transactionId) {
        try {
            BorrowTransaction transaction = borrowTransactionService.payFine(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error paying fine: " + e.getMessage());
        }
    }
    
    // Waive fine (Admin only)
    @PostMapping("/{transactionId}/waive-fine")
    public ResponseEntity<?> waiveFine(@PathVariable Long transactionId) {
        try {
            BorrowTransaction transaction = borrowTransactionService.waiveFine(transactionId);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error waiving fine: " + e.getMessage());
        }
    }
    
    // Update overdue transactions (Admin only)
    @PostMapping("/update-overdue")
    public ResponseEntity<?> updateOverdueTransactions() {
        try {
            borrowTransactionService.updateOverdueTransactions();
            return ResponseEntity.ok().body("Overdue transactions updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating overdue transactions: " + e.getMessage());
        }
    }
    
    // Get borrowing statistics (Admin only)
    @GetMapping("/statistics")
    public ResponseEntity<?> getBorrowingStatistics() {
        try {
            List<BorrowTransaction> allTransactions = borrowTransactionService.getAllTransactions();
            List<BorrowTransaction> overdueTransactions = borrowTransactionService.getOverdueTransactions();
            List<BorrowTransaction> unpaidFines = borrowTransactionService.getTransactionsWithUnpaidFines();
            List<Object[]> mostBorrowedBooks = borrowTransactionService.getMostBorrowedBooks();
            
            // Calculate active borrowings
            long activeBorrowings = allTransactions.stream()
                .filter(t -> t.getReturnDate() == null)
                .count();
            
            // Calculate total fines
            double totalUnpaidFines = unpaidFines.stream()
                .mapToDouble(t -> t.getFineAmount().doubleValue())
                .sum();
            
            BorrowingStatistics stats = new BorrowingStatistics(
                allTransactions.size(),
                (int) activeBorrowings,
                overdueTransactions.size(),
                unpaidFines.size(),
                totalUnpaidFines,
                mostBorrowedBooks.size() > 0 ? mostBorrowedBooks.subList(0, Math.min(5, mostBorrowedBooks.size())) : mostBorrowedBooks
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving statistics: " + e.getMessage());
        }
    }
    
    // Get most borrowed books
    @GetMapping("/most-borrowed")
    public ResponseEntity<List<Object[]>> getMostBorrowedBooks() {
        try {
            List<Object[]> mostBorrowedBooks = borrowTransactionService.getMostBorrowedBooks();
            return ResponseEntity.ok(mostBorrowedBooks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Inner classes for request/response DTOs
    public static class BorrowRequest {
        private Long userId;
        private Long bookId;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
    }
    
    public static class BorrowingStatistics {
        private int totalTransactions;
        private int activeBorrowings;
        private int overdueTransactions;
        private int unpaidFines;
        private double totalUnpaidFineAmount;
        private List<Object[]> mostBorrowedBooks;
        
        public BorrowingStatistics(int totalTransactions, int activeBorrowings, int overdueTransactions,
                                 int unpaidFines, double totalUnpaidFineAmount, List<Object[]> mostBorrowedBooks) {
            this.totalTransactions = totalTransactions;
            this.activeBorrowings = activeBorrowings;
            this.overdueTransactions = overdueTransactions;
            this.unpaidFines = unpaidFines;
            this.totalUnpaidFineAmount = totalUnpaidFineAmount;
            this.mostBorrowedBooks = mostBorrowedBooks;
        }
        
        // Getters and setters
        public int getTotalTransactions() { return totalTransactions; }
        public void setTotalTransactions(int totalTransactions) { this.totalTransactions = totalTransactions; }
        public int getActiveBorrowings() { return activeBorrowings; }
        public void setActiveBorrowings(int activeBorrowings) { this.activeBorrowings = activeBorrowings; }
        public int getOverdueTransactions() { return overdueTransactions; }
        public void setOverdueTransactions(int overdueTransactions) { this.overdueTransactions = overdueTransactions; }
        public int getUnpaidFines() { return unpaidFines; }
        public void setUnpaidFines(int unpaidFines) { this.unpaidFines = unpaidFines; }
        public double getTotalUnpaidFineAmount() { return totalUnpaidFineAmount; }
        public void setTotalUnpaidFineAmount(double totalUnpaidFineAmount) { this.totalUnpaidFineAmount = totalUnpaidFineAmount; }
        public List<Object[]> getMostBorrowedBooks() { return mostBorrowedBooks; }
        public void setMostBorrowedBooks(List<Object[]> mostBorrowedBooks) { this.mostBorrowedBooks = mostBorrowedBooks; }
    }
}