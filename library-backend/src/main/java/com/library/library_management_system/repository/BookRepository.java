// BookRepository.java
package com.library.library_management_system.repository;

import com.library.library_management_system.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // Find book by ISBN
    Optional<Book> findByIsbn(String isbn);
    
    // Check if ISBN exists
    boolean existsByIsbn(String isbn);
    
    // Find books by title (case insensitive)
    List<Book> findByTitleContainingIgnoreCase(String title);
    
    // Find books by author (case insensitive)
    List<Book> findByAuthorContainingIgnoreCase(String author);
    
    // Find books by category
    List<Book> findByCategory(String category);
    
    // Find books by status
    List<Book> findByStatus(Book.BookStatus status);
    
    // Find available books
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 AND b.status = 'ACTIVE'")
    List<Book> findAvailableBooks();
    
    // Search books by title, author, or ISBN
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:searchTerm% OR b.author LIKE %:searchTerm% OR b.isbn LIKE %:searchTerm%")
    List<Book> searchBooks(@Param("searchTerm") String searchTerm);
    
    // Find books by publication year range
    List<Book> findByPublicationYearBetween(Integer startYear, Integer endYear);
    
    // Find books with low availability (less than specified copies)
    @Query("SELECT b FROM Book b WHERE b.availableCopies <= :threshold AND b.status = 'ACTIVE'")
    List<Book> findBooksWithLowAvailability(@Param("threshold") Integer threshold);
    
    // Get all distinct categories
    @Query("SELECT DISTINCT b.category FROM Book b WHERE b.category IS NOT NULL ORDER BY b.category")
    List<String> findAllCategories();
    
    // Get all distinct authors
    @Query("SELECT DISTINCT b.author FROM Book b ORDER BY b.author")
    List<String> findAllAuthors();
}