package com.library.library_management_system.controller;

import com.library.library_management_system.entity.Book;
import com.library.library_management_system.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "http://localhost:3000")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    // Create new book (Admin only)
    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        try {
            Book createdBook = bookService.createBook(book);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating book: " + e.getMessage());
        }
    }
    
    // Get all books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get book by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try {
            Optional<Book> book = bookService.findById(id);
            if (book.isPresent()) {
                return ResponseEntity.ok(book.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving book: " + e.getMessage());
        }
    }
    
    // Get book by ISBN
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<?> getBookByIsbn(@PathVariable String isbn) {
        try {
            Optional<Book> book = bookService.findByIsbn(isbn);
            if (book.isPresent()) {
                return ResponseEntity.ok(book.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving book: " + e.getMessage());
        }
    }
    
    // Search books
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam(required = false) String searchTerm) {
        try {
            List<Book> books = bookService.searchBooks(searchTerm);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get available books
    @GetMapping("/available")
    public ResponseEntity<List<Book>> getAvailableBooks() {
        try {
            List<Book> books = bookService.getAvailableBooks();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get books by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Book>> getBooksByCategory(@PathVariable String category) {
        try {
            List<Book> books = bookService.getBooksByCategory(category);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get all categories
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        try {
            List<String> categories = bookService.getAllCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get all authors
    @GetMapping("/authors")
    public ResponseEntity<List<String>> getAllAuthors() {
        try {
            List<String> authors = bookService.getAllAuthors();
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Update book (Admin only)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        try {
            Book updatedBook = bookService.updateBook(id, bookDetails);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating book: " + e.getMessage());
        }
    }
    
    // Delete book (Admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok().body("Book deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting book: " + e.getMessage());
        }
    }
    
    // Check book availability
    @GetMapping("/{id}/availability")
    public ResponseEntity<?> checkBookAvailability(@PathVariable Long id) {
        try {
            boolean isAvailable = bookService.isBookAvailable(id);
            return ResponseEntity.ok().body(new BookAvailabilityResponse(isAvailable));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error checking availability: " + e.getMessage());
        }
    }
    
    // Update book copies (Admin only)
    @PutMapping("/{id}/copies")
    public ResponseEntity<?> updateBookCopies(@PathVariable Long id, @RequestBody UpdateCopiesRequest request) {
        try {
            Book updatedBook = bookService.updateBookCopies(
                id, 
                request.getTotalCopies(), 
                request.getAvailableCopies()
            );
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating book copies: " + e.getMessage());
        }
    }
    
    // Get books with low availability (Admin only)
    @GetMapping("/low-availability")
    public ResponseEntity<List<Book>> getBooksWithLowAvailability(@RequestParam(defaultValue = "2") int threshold) {
        try {
            List<Book> books = bookService.getBooksWithLowAvailability(threshold);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Advanced search with filters
    @GetMapping("/advanced-search")
    public ResponseEntity<List<Book>> advancedSearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer publicationYear,
            @RequestParam(required = false) Boolean availableOnly) {
        try {
            List<Book> books;
            
            if (title != null && !title.trim().isEmpty()) {
                books = bookService.searchBooks(title);
            } else if (author != null && !author.trim().isEmpty()) {
                books = bookService.searchBooks(author);
            } else if (category != null && !category.trim().isEmpty()) {
                books = bookService.getBooksByCategory(category);
            } else {
                books = bookService.getAllBooks();
            }
            
            // Filter by availability if requested
            if (availableOnly != null && availableOnly) {
                books = books.stream()
                    .filter(Book::isAvailable)
                    .toList();
            }
            
            // Filter by publication year if specified
            if (publicationYear != null) {
                books = books.stream()
                    .filter(book -> book.getPublicationYear() != null && 
                                   book.getPublicationYear().equals(publicationYear))
                    .toList();
            }
            
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get book statistics (Admin only)
    @GetMapping("/statistics")
    public ResponseEntity<?> getBookStatistics() {
        try {
            List<Book> allBooks = bookService.getAllBooks();
            List<Book> availableBooks = bookService.getAvailableBooks();
            List<String> categories = bookService.getAllCategories();
            List<String> authors = bookService.getAllAuthors();
            
            BookStatistics stats = new BookStatistics(
                allBooks.size(),
                availableBooks.size(),
                allBooks.size() - availableBooks.size(),
                categories.size(),
                authors.size()
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving statistics: " + e.getMessage());
        }
    }
    
    // Inner classes for request/response DTOs
    public static class BookAvailabilityResponse {
        private boolean available;
        
        public BookAvailabilityResponse(boolean available) {
            this.available = available;
        }
        
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
    }
    
    public static class UpdateCopiesRequest {
        private int totalCopies;
        private int availableCopies;
        
        public int getTotalCopies() { return totalCopies; }
        public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
        public int getAvailableCopies() { return availableCopies; }
        public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    }
    
    public static class BookStatistics {
        private int totalBooks;
        private int availableBooks;
        private int borrowedBooks;
        private int totalCategories;
        private int totalAuthors;
        
        public BookStatistics(int totalBooks, int availableBooks, int borrowedBooks, 
                             int totalCategories, int totalAuthors) {
            this.totalBooks = totalBooks;
            this.availableBooks = availableBooks;
            this.borrowedBooks = borrowedBooks;
            this.totalCategories = totalCategories;
            this.totalAuthors = totalAuthors;
        }
        
        // Getters and setters
        public int getTotalBooks() { return totalBooks; }
        public void setTotalBooks(int totalBooks) { this.totalBooks = totalBooks; }
        public int getAvailableBooks() { return availableBooks; }
        public void setAvailableBooks(int availableBooks) { this.availableBooks = availableBooks; }
        public int getBorrowedBooks() { return borrowedBooks; }
        public void setBorrowedBooks(int borrowedBooks) { this.borrowedBooks = borrowedBooks; }
        public int getTotalCategories() { return totalCategories; }
        public void setTotalCategories(int totalCategories) { this.totalCategories = totalCategories; }
        public int getTotalAuthors() { return totalAuthors; }
        public void setTotalAuthors(int totalAuthors) { this.totalAuthors = totalAuthors; }
    }
}