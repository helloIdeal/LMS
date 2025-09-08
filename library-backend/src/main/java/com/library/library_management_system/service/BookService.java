package com.library.library_management_system.service;

import com.library.library_management_system.entity.Book;
import com.library.library_management_system.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    // Create new book
    public Book createBook(Book book) {
        // Check if ISBN already exists
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuntimeException("Book with this ISBN already exists");
        }
        
        return bookRepository.save(book);
    }
    
    // Get all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    // Find book by ID
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }
    
    // Find book by ISBN
    public Optional<Book> findByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
    
    // Search books
    public List<Book> searchBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookRepository.searchBooks(searchTerm);
    }
    
    // Get available books
    public List<Book> getAvailableBooks() {
        return bookRepository.findAvailableBooks();
    }
    
    // Get books by category
    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategory(category);
    }
    
    // Get all categories
    public List<String> getAllCategories() {
        return bookRepository.findAllCategories();
    }
    
    // Get all authors
    public List<String> getAllAuthors() {
        return bookRepository.findAllAuthors();
    }
    
    // Update book
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setCategory(bookDetails.getCategory());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setTotalCopies(bookDetails.getTotalCopies());
        book.setPublisher(bookDetails.getPublisher());
        book.setDescription(bookDetails.getDescription());
        book.setShelfLocation(bookDetails.getShelfLocation());
        book.setStatus(bookDetails.getStatus());
        
        // Ensure available copies don't exceed total copies
        if (book.getAvailableCopies() > bookDetails.getTotalCopies()) {
            book.setAvailableCopies(bookDetails.getTotalCopies());
        }
        
        return bookRepository.save(book);
    }
    
    // Delete book
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found");
        }
        bookRepository.deleteById(id);
    }
    
    // Check if book is available for borrowing
    public boolean isBookAvailable(Long bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        return book.isPresent() && book.get().isAvailable();
    }
    
    // Borrow a copy (decrease available count)
    public Book borrowBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available for borrowing");
        }
        
        book.borrowCopy();
        return bookRepository.save(book);
    }
    
    // Return a copy (increase available count)
    public Book returnBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        book.returnCopy();
        return bookRepository.save(book);
    }
    
    // Get books with low availability
    public List<Book> getBooksWithLowAvailability(int threshold) {
        return bookRepository.findBooksWithLowAvailability(threshold);
    }
    
    // Update book copies
    public Book updateBookCopies(Long bookId, int totalCopies, int availableCopies) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (availableCopies > totalCopies) {
            throw new RuntimeException("Available copies cannot exceed total copies");
        }
        
        book.setTotalCopies(totalCopies);
        book.setAvailableCopies(availableCopies);
        
        return bookRepository.save(book);
    }
}