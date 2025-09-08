// BorrowTransactionRepository.java
package com.library.library_management_system.repository;

import com.library.library_management_system.entity.BorrowTransaction;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowTransactionRepository extends JpaRepository<BorrowTransaction, Long> {
    
    // Find transactions by user
    List<BorrowTransaction> findByUser(User user);
    
    // Find transactions by book
    List<BorrowTransaction> findByBook(Book book);
    
    // Find transactions by status
    List<BorrowTransaction> findByStatus(BorrowTransaction.TransactionStatus status);
    
    // Find active borrowings by user (not returned)
    @Query("SELECT bt FROM BorrowTransaction bt WHERE bt.user = :user AND bt.returnDate IS NULL")
    List<BorrowTransaction> findActiveBorrowingsByUser(@Param("user") User user);
    
    // Count active borrowings by user
    @Query("SELECT COUNT(bt) FROM BorrowTransaction bt WHERE bt.user = :user AND bt.returnDate IS NULL")
    Long countActiveBorrowingsByUser(@Param("user") User user);
    
    // Find overdue transactions
    @Query("SELECT bt FROM BorrowTransaction bt WHERE bt.dueDate < :currentDate AND bt.returnDate IS NULL")
    List<BorrowTransaction> findOverdueTransactions(@Param("currentDate") LocalDate currentDate);
    
    // Find transactions due soon (within specified days)
    @Query("SELECT bt FROM BorrowTransaction bt WHERE bt.dueDate BETWEEN :startDate AND :endDate AND bt.returnDate IS NULL")
    List<BorrowTransaction> findTransactionsDueSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find transactions by date range
    List<BorrowTransaction> findByBorrowDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Find transactions with unpaid fines
    @Query("SELECT bt FROM BorrowTransaction bt WHERE bt.fineAmount > 0 AND bt.finePaid = false")
    List<BorrowTransaction> findTransactionsWithUnpaidFines();
    
    // Get borrowing history for a user (all transactions)
    @Query("SELECT bt FROM BorrowTransaction bt WHERE bt.user = :user ORDER BY bt.borrowDate DESC")
    List<BorrowTransaction> findBorrowingHistoryByUser(@Param("user") User user);
    
    // Check if user has already borrowed a specific book and not returned
    @Query("SELECT COUNT(bt) > 0 FROM BorrowTransaction bt WHERE bt.user = :user AND bt.book = :book AND bt.returnDate IS NULL")
    boolean hasUserBorrowedBook(@Param("user") User user, @Param("book") Book book);
    
    // Find most borrowed books
    @Query("SELECT bt.book, COUNT(bt) as borrowCount FROM BorrowTransaction bt GROUP BY bt.book ORDER BY borrowCount DESC")
    List<Object[]> findMostBorrowedBooks();
    
    // Find transactions by user ID for easier API queries
    @Query("SELECT bt FROM BorrowTransaction bt WHERE bt.user.id = :userId")
    List<BorrowTransaction> findByUserId(@Param("userId") Long userId);
}