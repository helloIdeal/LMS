// src/components/HomePage.js
import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import Register from './Register';
import Login from './Login';
import VideoBackground from './VideoBackground';
import './HomePage.css';

const HomePage = () => {
  const [isRegisterOpen, setIsRegisterOpen] = useState(false);
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [showDropdown, setShowDropdown] = useState(false);
  const dropdownRef = useRef(null);
  const searchContainerRef = useRef(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target) &&
          searchContainerRef.current && !searchContainerRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) return;

    setIsSearching(true);
    try {
      const response = await axios.get(`http://localhost:8080/api/books/search?searchTerm=${encodeURIComponent(searchTerm)}`);
      setSearchResults(response.data);
      setShowDropdown(true);
    } catch (error) {
      console.error('Search error:', error);
      setSearchResults([]);
      setShowDropdown(true);
    } finally {
      setIsSearching(false);
    }
  };

  const handleBookClick = (book) => {
    alert('Please log in to borrow books');
    // Dropdown stays open as per requirement
  };

  const openRegister = () => {
    setIsRegisterOpen(true);
  };

  const closeRegister = () => {
    setIsRegisterOpen(false);
  };

  const openLogin = () => {
    setIsLoginOpen(true);
  };

  const closeLogin = () => {
    setIsLoginOpen(false);
  };

  return (
    <div className="homepage">
      {/* Video Background Component */}
      <VideoBackground />

      <header className="header">
        <div></div>
        <div className="nav-right">
          <button className="nav-btn" onClick={openLogin}>
            Login
          </button>
          <button className="nav-btn" onClick={openRegister}>
            Register
          </button>
        </div>
      </header>

      <main className="main-content">
        <div className="search-section">
          <form onSubmit={handleSearch} className="search-form">
            <div className="search-container" ref={searchContainerRef}>
              <input
                type="text"
                placeholder="Search for your books here..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="search-input"
                disabled={isSearching}
              />
              <button 
                type="submit" 
                className="search-btn"
                disabled={isSearching || !searchTerm.trim()}
              >
                <svg className="search-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M21 21L16.514 16.506L21 21ZM19 10.5C19 15.194 15.194 19 10.5 19C5.806 19 2 15.194 2 10.5C2 5.806 5.806 2 10.5 2C15.194 2 19 5.806 19 10.5Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
              </button>
            </div>

            {/* Search Results Dropdown */}
            {showDropdown && (
              <div className="search-dropdown" ref={dropdownRef}>
                {isSearching ? (
                  <div className="dropdown-loading">
                    <p>Searching for books...</p>
                  </div>
                ) : searchResults.length > 0 ? (
                  <>
                    <div className="dropdown-header">
                      <h3>Search Results ({searchResults.length} books found)</h3>
                      <button 
                        className="dropdown-close-btn"
                        onClick={() => setShowDropdown(false)}
                      >
                        ×
                      </button>
                    </div>
                    <div className="dropdown-books">
                      {searchResults.map(book => (
                        <div 
                          key={book.id} 
                          className="dropdown-book-card"
                          onClick={() => handleBookClick(book)}
                        >
                          <h4>{book.title}</h4>
                          <p><strong>Author:</strong> {book.author}</p>
                          <p><strong>Category:</strong> {book.category}</p>
                          <p><strong>ISBN:</strong> {book.isbn}</p>
                          <div className="book-availability">
                            <span className={`availability-badge ${book.availableCopies > 0 ? 'available' : 'unavailable'}`}>
                              {book.availableCopies > 0 ? `${book.availableCopies} Available` : 'Not Available'}
                            </span>
                          </div>
                        </div>
                      ))}
                    </div>
                  </>
                ) : (
                  <div className="dropdown-no-results">
                    <h3>No Results Found</h3>
                    <p>No books found matching "{searchTerm}". Try different keywords.</p>
                  </div>
                )}
              </div>
            )}
          </form>
        </div>

        <div className="welcome-section">
          <div className="welcome-message">
            <h2>Welcome to The Ideal Library</h2>
            <p>Search for books using the search bar above</p>
          </div>
        </div>
      </main>

      {/* Register Modal */}
      <Register isOpen={isRegisterOpen} onClose={closeRegister} />
      
      {/* Login Modal */}
      <Login isOpen={isLoginOpen} onClose={closeLogin} />
    </div>
  );
};

export default HomePage;