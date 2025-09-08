package com.library.library_management_system.service;

import com.library.library_management_system.entity.BorrowTransaction;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.entity.Book;
import com.library.library_management_system.repository.BorrowTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BorrowTransactionService {
    
    @Autowired
    private BorrowTransactionRepository borrowTransactionRepository;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private UserService userService;
    
    private static final int MAX_BOOKS_PER_USER = 3;
    private static final int LOAN_PERIOD_DAYS = 14;
    
    // Borrow a book
    public BorrowTransaction borrowBook(Long userId, Long bookId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        // Check if user membership is valid
        if (!userService.isMembershipValid(user)) {
            throw new RuntimeException("User membership has expired");
        }
        
        // Check if user has reached borrowing limit
        Long activeBorrowings = borrowTransactionRepository.countActiveBorrowingsByUser(user);
        if (activeBorrowings >= MAX_BOOKS_PER_USER) {
            throw new RuntimeException("User has reached maximum borrowing limit");
        }
        
        // Check if book is available
        if (!bookService.isBookAvailable(bookId)) {
            throw new RuntimeException("Book is not available for borrowing");
        }
        
        // Check if user has already borrowed this book
        if (borrowTransactionRepository.hasUserBorrowedBook(user, book)) {
            throw new RuntimeException("User has already borrowed this book");
        }
        
        // Create transaction
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = borrowDate.plusDays(LOAN_PERIOD_DAYS);
        
        BorrowTransaction transaction = new BorrowTransaction(user, book, borrowDate, dueDate);
        
        // Update book availability
        bookService.borrowBook(bookId);
        
        return borrowTransactionRepository.save(transaction);
    }
    
    // Return a book
    public BorrowTransaction returnBook(Long transactionId) {
        BorrowTransaction transaction = borrowTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (transaction.getReturnDate() != null) {
            throw new RuntimeException("Book has already been returned");
        }
        
        // Mark as returned
        transaction.returnBook();
        
        // Update book availability
        bookService.returnBook(transaction.getBook().getId());
        
        return borrowTransactionRepository.save(transaction);
    }
    
    // Renew a book
    public BorrowTransaction renewBook(Long transactionId) {
        BorrowTransaction transaction = borrowTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        if (!transaction.canRenew()) {
            throw new RuntimeException("Book cannot be renewed. Maximum renewals reached or book is overdue.");
        }
        
        transaction.renewTransaction();
        return borrowTransactionRepository.save(transaction);
    }
    
    // Get user's borrowing history
    public List<BorrowTransaction> getUserBorrowingHistory(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return borrowTransactionRepository.findBorrowingHistoryByUser(user);
    }
    
    // Get user's active borrowings
    public List<BorrowTransaction> getUserActiveBorrowings(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return borrowTransactionRepository.findActiveBorrowingsByUser(user);
    }
    
    // Get all transactions
    public List<BorrowTransaction> getAllTransactions() {
        return borrowTransactionRepository.findAll();
    }
    
    // Get overdue transactions
    public List<BorrowTransaction> getOverdueTransactions() {
        return borrowTransactionRepository.findOverdueTransactions(LocalDate.now());
    }
    
    // Get transactions due soon
    public List<BorrowTransaction> getTransactionsDueSoon(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return borrowTransactionRepository.findTransactionsDueSoon(today, endDate);
    }
    
    // Get transactions with unpaid fines
    public List<BorrowTransaction> getTransactionsWithUnpaidFines() {
        return borrowTransactionRepository.findTransactionsWithUnpaidFines();
    }
    
    // Update overdue transactions and calculate fines
    public void updateOverdueTransactions() {
        List<BorrowTransaction> overdueTransactions = getOverdueTransactions();
        
        for (BorrowTransaction transaction : overdueTransactions) {
            transaction.markOverdue();
            borrowTransactionRepository.save(transaction);
        }
    }
    
    // Pay fine
    public BorrowTransaction payFine(Long transactionId) {
        BorrowTransaction transaction = borrowTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        transaction.setFinePaid(true);
        return borrowTransactionRepository.save(transaction);
    }
    
    // Waive fine
    public BorrowTransaction waiveFine(Long transactionId) {
        BorrowTransaction transaction = borrowTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        transaction.setFineAmount(java.math.BigDecimal.ZERO);
        transaction.setFinePaid(true);
        return borrowTransactionRepository.save(transaction);
    }
    
    // Get borrowing statistics
    public List<Object[]> getMostBorrowedBooks() {
        return borrowTransactionRepository.findMostBorrowedBooks();
    }
    
    // Find transaction by ID
    public Optional<BorrowTransaction> findById(Long id) {
        return borrowTransactionRepository.findById(id);
    }
    
    // Get transactions by status
    public List<BorrowTransaction> getTransactionsByStatus(BorrowTransaction.TransactionStatus status) {
        return borrowTransactionRepository.findByStatus(status);
    }
    
    // Get transactions by date range
    public List<BorrowTransaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return borrowTransactionRepository.findByBorrowDateBetween(startDate, endDate);
    }
}