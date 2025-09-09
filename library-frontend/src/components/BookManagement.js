// src/components/BookManagement.js
import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import BookManagementAddEdit from './BookManagementAddEdit';
import VideoBackground from './VideoBackground';
import './BookManagement.css';

const BookManagement = () => {
  const navigate = useNavigate();
  const [books, setBooks] = useState([]);
  const [filteredBooks, setFilteredBooks] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedBook, setSelectedBook] = useState(null);
  const [showAddEditModal, setShowAddEditModal] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);
  const [booksPerPage] = useState(5);

  // Search state
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');

  // Calculate pagination
  const indexOfLastBook = currentPage * booksPerPage;
  const indexOfFirstBook = indexOfLastBook - booksPerPage;
  const currentBooks = filteredBooks.slice(indexOfFirstBook, indexOfLastBook);
  const totalPages = Math.ceil(filteredBooks.length / booksPerPage);

  const API_BASE_URL = 'http://localhost:8080/api';

  const fetchBooks = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_BASE_URL}/books`);
      
      // Handle response data properly
      let bookData = response.data;
      
      // If the response is a string, parse it as JSON
      if (typeof response.data === 'string') {
        try {
          bookData = JSON.parse(response.data);
        } catch (parseError) {
          console.error('Failed to parse JSON:', parseError);
          setError('Failed to parse book data');
          return;
        }
      }
      
      console.log('Books fetched:', bookData); // Debug log
      console.log('First book structure:', JSON.stringify(bookData[0], null, 2)); // Debug structure
      console.log('Array length:', bookData.length);
      console.log('Type of first element:', typeof bookData[0]);
      
      setBooks(bookData);
      setError('');
    } catch (err) {
      setError('Failed to fetch books');
      console.error('Error fetching books:', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/books/categories`);
      setCategories(response.data);
    } catch (err) {
      console.error('Error fetching categories:', err);
    }
  };

  const filterBooks = useCallback(() => {
    let filtered = [...books];

    // Filter by search term (title, author, ISBN)
    if (searchTerm.trim()) {
      filtered = filtered.filter(book =>
        (book.title && book.title.toLowerCase().includes(searchTerm.toLowerCase())) ||
        (book.author && book.author.toLowerCase().includes(searchTerm.toLowerCase())) ||
        (book.isbn && book.isbn.toLowerCase().includes(searchTerm.toLowerCase()))
      );
    }

    // Filter by category
    if (selectedCategory) {
      filtered = filtered.filter(book => book.category === selectedCategory);
    }

    console.log('Filtered books:', filtered); // Debug log
    setFilteredBooks(filtered);
    setCurrentPage(1); // Reset to first page when filtering
  }, [books, searchTerm, selectedCategory]);

  useEffect(() => {
    fetchBooks();
    fetchCategories();
  }, []);

  useEffect(() => {
    filterBooks();
  }, [filterBooks]);

  const handleRowClick = (book) => {
    setSelectedBook(selectedBook?.id === book.id ? null : book);
  };

  const handleAddBook = () => {
    setSelectedBook(null);
    setIsEditMode(false);
    setShowAddEditModal(true);
  };

  const handleUpdateBook = () => {
    if (!selectedBook) {
      alert('Please select a book to update');
      return;
    }
    setIsEditMode(true);
    setShowAddEditModal(true);
  };

  const handleDeleteBook = async () => {
    if (!selectedBook) {
      alert('Please select a book to delete');
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete "${selectedBook.title}"? This action cannot be undone.`
    );

    if (confirmed) {
      try {
        await axios.delete(`${API_BASE_URL}/books/${selectedBook.id}`);
        setBooks(books.filter(book => book.id !== selectedBook.id));
        setSelectedBook(null);
        alert('Book deleted successfully');
      } catch (err) {
        alert('Failed to delete book');
        console.error('Error deleting book:', err);
      }
    }
  };

  const handleSaveBook = async (bookData) => {
    try {
      if (isEditMode) {
        const response = await axios.put(`${API_BASE_URL}/books/${selectedBook.id}`, bookData);
        setBooks(books.map(book => 
          book.id === selectedBook.id ? response.data : book
        ));
        alert('Book updated successfully');
      } else {
        const response = await axios.post(`${API_BASE_URL}/books`, bookData);
        setBooks([...books, response.data]);
        alert('Book added successfully');
      }
      setShowAddEditModal(false);
      setSelectedBook(null);
    } catch (err) {
      alert(err.response?.data || 'Failed to save book');
      console.error('Error saving book:', err);
    }
  };

  const handlePageChange = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const handleHome = () => {
    navigate('/');
  };

  const handleAdminDashboard = () => {
    navigate('/admin-dashboard');
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    navigate('/');
  };

  const getStatusBadge = (status) => {
    const statusClass = {
      'ACTIVE': 'status-active',
      'INACTIVE': 'status-inactive',
      'DAMAGED': 'status-damaged',
      'LOST': 'status-lost'
    };
    return <span className={`status-badge ${statusClass[status] || 'status-active'}`}>{status}</span>;
  };

  if (loading) {
    return <div className="loading">Loading books...</div>;
  }

  console.log('Current books to display:', currentBooks); // Debug log

  return (
    <div className="book-management">
      {/* Video Background Component */}
      <VideoBackground />

      {/* Navigation Bar */}
      <nav className="book-navbar">
        <div className="nav-left">
          <button className="nav-btn home-btn" onClick={handleHome}>Home</button>
          <button className="nav-btn admin-btn" onClick={handleAdminDashboard}>Admin Dashboard</button>
        </div>
        <div className="nav-right">
          <button className="nav-btn logout-btn" onClick={handleLogout}>Logout</button>
        </div>
      </nav>

      {/* Main Content */}
      <main className="book-main">
        <div className="book-header">
          <h1>BOOK MANAGEMENT - LIST</h1>
        </div>

        {error && <div className="error-message">{error}</div>}

        {/* Search and Filter Section */}
        <div className="search-filter-section">
          <div className="search-controls">
            <input
              type="text"
              placeholder="Search by Title, Author, or ISBN..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
            <select
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              className="category-filter"
            >
              <option value="">All Categories</option>
              {categories.map((category, index) => (
                <option key={`category-${index}`} value={category}>{category}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Books Table */}
        <div className="books-table-container">
          <table className="books-table">
            <thead>
              <tr>
                <th>ISBN</th>
                <th>Title</th>
                <th>Author</th>
                <th>Category</th>
                <th>Year</th>
                <th>Total</th>
                <th>Available</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {currentBooks.length === 0 ? (
                <tr>
                  <td colSpan="8" className="no-books">
                    {books.length === 0 ? 'No books found' : 'No books match your search criteria'}
                  </td>
                </tr>
              ) : (
                currentBooks.map((book) => (
                  <tr
                    key={`book-${book.id}`}
                    className={selectedBook?.id === book.id ? 'selected-row' : ''}
                    onClick={() => handleRowClick(book)}
                  >
                    <td>{book.isbn || 'N/A'}</td>
                    <td>{book.title || 'N/A'}</td>
                    <td>{book.author || 'N/A'}</td>
                    <td>{book.category || 'N/A'}</td>
                    <td>{book.publicationYear || 'N/A'}</td>
                    <td>{book.totalCopies || 0}</td>
                    <td>{book.availableCopies || 0}</td>
                    <td>{getStatusBadge(book.status || 'ACTIVE')}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="pagination">
            {Array.from({ length: totalPages }, (_, index) => (
              <button
                key={`page-${index + 1}`}
                className={`page-btn ${currentPage === index + 1 ? 'active' : ''}`}
                onClick={() => handlePageChange(index + 1)}
              >
                {index + 1}
              </button>
            ))}
          </div>
        )}

        {/* Action Buttons */}
        <div className="action-buttons">
          <button className="action-btn add-btn" onClick={handleAddBook}>
            Add New Book
          </button>
          <button 
            className="action-btn update-btn" 
            onClick={handleUpdateBook}
            disabled={!selectedBook}
          >
            Update Book
          </button>
          <button 
            className="action-btn delete-btn" 
            onClick={handleDeleteBook}
            disabled={!selectedBook}
          >
            Delete Book
          </button>
        </div>

        {/* Results Info */}
        <div className="results-info">
          Showing {currentBooks.length > 0 ? indexOfFirstBook + 1 : 0} to {Math.min(indexOfLastBook, filteredBooks.length)} of {filteredBooks.length} books
        </div>
      </main>

      {/* Add/Edit Modal */}
      {showAddEditModal && (
        <BookManagementAddEdit
          book={isEditMode ? selectedBook : null}
          isEditMode={isEditMode}
          onSave={handleSaveBook}
          onCancel={() => setShowAddEditModal(false)}
        />
      )}
    </div>
  );
};

export default BookManagement;