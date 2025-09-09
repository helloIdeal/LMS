// src/components/BookSearch.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './BookSearch.css';

const BookSearch = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [books, setBooks] = useState([]);
  const [filteredBooks, setFilteredBooks] = useState([]);
  const [selectedBook, setSelectedBook] = useState(null);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [user, setUser] = useState(null);
  const [borrowAction, setBorrowAction] = useState(''); // 'borrowing' or 'reserving'
  const navigate = useNavigate();

  useEffect(() => {
    // Check authentication
    const userData = localStorage.getItem('user');
    if (!userData) {
      navigate('/');
      return;
    }

    const parsedUser = JSON.parse(userData);
    if (parsedUser.role !== 'MEMBER') {
      navigate('/');
      return;
    }

    setUser(parsedUser);
    fetchInitialData();
  }, [navigate]);

  const fetchInitialData = async () => {
    try {
      setLoading(true);
      
      // Fetch all books
      const booksResponse = await axios.get('http://localhost:8080/api/books');
      setBooks(booksResponse.data);
      setFilteredBooks(booksResponse.data);

      // Fetch categories
      const categoriesResponse = await axios.get('http://localhost:8080/api/books/categories');
      setCategories(categoriesResponse.data);

    } catch (err) {
      console.error('Error fetching data:', err);
      setError('Failed to load books data');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    filterBooks();
  };

  const filterBooks = () => {
    let filtered = books;

    // Filter by search term
    if (searchTerm.trim()) {
      const term = searchTerm.toLowerCase();
      filtered = filtered.filter(book => 
        book.title.toLowerCase().includes(term) ||
        book.author.toLowerCase().includes(term) ||
        (book.isbn && book.isbn.toLowerCase().includes(term))
      );
    }

    // Filter by category
    if (selectedCategory) {
      filtered = filtered.filter(book => book.category === selectedCategory);
    }

    setFilteredBooks(filtered);
  };

  const handleCategoryChange = (e) => {
    setSelectedCategory(e.target.value);
    // Auto-filter when category changes
    setTimeout(() => {
      filterBooks();
    }, 0);
  };

  const handleBookSelect = (book) => {
    if (selectedBook && selectedBook.id === book.id) {
      setSelectedBook(null); // Deselect if clicking the same book
    } else {
      setSelectedBook(book);
    }
  };

  const handleBorrowBook = async () => {
    if (!selectedBook || !user) return;

    try {
      setBorrowAction('borrowing');
      
      const response = await axios.post('http://localhost:8080/api/transactions/borrow', {
        userId: user.id,
        bookId: selectedBook.id
      });

      alert(`Successfully borrowed "${selectedBook.title}"! Due date: ${new Date(response.data.dueDate).toLocaleDateString()}`);
      
      // Refresh books data to update availability
      await fetchInitialData();
      setSelectedBook(null);
      
    } catch (err) {
      console.error('Borrow error:', err);
      if (err.response?.data) {
        alert(`Failed to borrow book: ${err.response.data}`);
      } else {
        alert('Failed to borrow book. Please try again.');
      }
    } finally {
      setBorrowAction('');
    }
  };

  const handleReserveBook = async () => {
    if (!selectedBook || !user) return;

    try {
      setBorrowAction('reserving');
      
      // Note: This would use a reservation endpoint once implemented
      // For now, show placeholder
      alert(`Book reservation functionality coming soon!\n\nYou would be added to the waiting list for "${selectedBook.title}".`);
      
    } catch (err) {
      console.error('Reserve error:', err);
      alert('Failed to reserve book. Please try again.');
    } finally {
      setBorrowAction('');
    }
  };

  const handleBackToDashboard = () => {
    navigate('/member-dashboard');
  };

  const getBookStatus = (book) => {
    if (book.availableCopies > 0) {
      return { status: 'available', text: 'Available' };
    } else {
      return { status: 'unavailable', text: 'Not Available' };
    }
  };

  if (loading && books.length === 0) {
    return (
      <div className="book-search">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading books...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="book-search">
      {/* Header */}
      <div className="search-header">
        <div className="header-left">
          <h1>Search & Borrow Books</h1>
          <p>Find and borrow books from our library collection</p>
        </div>
        <div className="header-right">
          <button onClick={handleBackToDashboard} className="back-btn">
            Back to Dashboard
          </button>
        </div>
      </div>

      {/* Search Section */}
      <div className="search-section">
        <form onSubmit={handleSearch} className="search-form">
          <div className="search-row">
            <div className="search-input-group">
              <input
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Search by title, author, or ISBN..."
                className="search-input"
              />
              <button type="submit" className="search-btn" disabled={loading}>
                <span className="search-icon">🔍</span>
                Search
              </button>
            </div>
            
            <div className="filter-group">
              <select
                value={selectedCategory}
                onChange={handleCategoryChange}
                className="category-filter"
              >
                <option value="">All Categories</option>
                {categories.map(category => (
                  <option key={category} value={category}>
                    {category}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </form>
      </div>

      {/* Results Section */}
      <div className="results-section">
        <div className="results-header">
          <h2>Search Results ({filteredBooks.length} books found)</h2>
          {selectedBook && (
            <div className="selected-indicator">
              Selected: "{selectedBook.title}"
            </div>
          )}
        </div>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {filteredBooks.length === 0 && !loading ? (
          <div className="no-results">
            <h3>No books found</h3>
            <p>Try adjusting your search terms or category filter.</p>
          </div>
        ) : (
          <div className="books-grid">
            {filteredBooks.map(book => {
              const bookStatus = getBookStatus(book);
              const isSelected = selectedBook && selectedBook.id === book.id;
              
              return (
                <div
                  key={book.id}
                  className={`book-card ${isSelected ? 'selected' : ''} ${bookStatus.status}`}
                  onClick={() => handleBookSelect(book)}
                >
                  <div className="book-header">
                    <h3 className="book-title">{book.title}</h3>
                    <span className={`availability-badge ${bookStatus.status}`}>
                      {bookStatus.text}
                    </span>
                  </div>
                  
                  <div className="book-details">
                    <p><strong>Author:</strong> {book.author}</p>
                    <p><strong>Category:</strong> {book.category || 'Uncategorized'}</p>
                    <p><strong>ISBN:</strong> {book.isbn}</p>
                    {book.publicationYear && (
                      <p><strong>Year:</strong> {book.publicationYear}</p>
                    )}
                    <p><strong>Available Copies:</strong> {book.availableCopies} / {book.totalCopies}</p>
                  </div>

                  {book.description && (
                    <div className="book-description">
                      <p>{book.description.length > 150 
                        ? `${book.description.substring(0, 150)}...` 
                        : book.description}
                      </p>
                    </div>
                  )}

                  {isSelected && (
                    <div className="selection-indicator">
                      ✓ Selected
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Action Buttons */}
      {selectedBook && (
        <div className="action-section">
          <div className="action-card">
            <h3>Selected Book: "{selectedBook.title}"</h3>
            <p>By {selectedBook.author}</p>
            
            <div className="action-buttons">
              {getBookStatus(selectedBook).status === 'available' ? (
                <button
                  onClick={handleBorrowBook}
                  className="borrow-btn"
                  disabled={borrowAction === 'borrowing'}
                >
                  {borrowAction === 'borrowing' ? 'Borrowing...' : 'Borrow Book'}
                </button>
              ) : (
                <button
                  onClick={handleReserveBook}
                  className="reserve-btn"
                  disabled={borrowAction === 'reserving'}
                >
                  {borrowAction === 'reserving' ? 'Reserving...' : 'Reserve Book'}
                </button>
              )}
              
              <button
                onClick={() => setSelectedBook(null)}
                className="cancel-selection-btn"
                disabled={borrowAction}
              >
                Cancel Selection
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default BookSearch;