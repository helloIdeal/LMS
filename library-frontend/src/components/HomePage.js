// src/components/HomePage.js
import React, { useState } from 'react';
import axios from 'axios';
import Register from './Register';
import Login from './Login';
import './HomePage.css';

const HomePage = () => {
  const [isRegisterOpen, setIsRegisterOpen] = useState(false);
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [isSearching, setIsSearching] = useState(false);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) return;

    setIsSearching(true);
    try {
      const response = await axios.get(`http://localhost:8080/api/books/search?searchTerm=${encodeURIComponent(searchTerm)}`);
      setSearchResults(response.data);
    } catch (error) {
      console.error('Search error:', error);
      setSearchResults([]);
    } finally {
      setIsSearching(false);
    }
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
      <div className="video-background">
        <div className="video-placeholder">
          <video autoPlay muted loop className="video-bg">
            <source src="/library-bg.mp4" type="video/mp4" />
            Your browser does not support the video tag.
          </video>
        </div>
      </div>
      
      <div className="overlay"></div>

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
            <div className="search-container">
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
                🔍
              </button>
            </div>
          </form>
        </div>

        <div className="welcome-section">
          <div className="welcome-message">
            <h2>Welcome to the Library Management System</h2>
            <p>Search for books using the search bar above</p>
          </div>
        </div>

        {/* Search Results Section */}
        {searchResults.length > 0 && (
          <div className="search-results">
            <h2>Search Results ({searchResults.length} books found)</h2>
            <div className="books-grid">
              {searchResults.map(book => (
                <div key={book.id} className="book-card">
                  <h3>{book.title}</h3>
                  <p><strong>Author:</strong> {book.author}</p>
                  <p><strong>Category:</strong> {book.category}</p>
                  <p><strong>ISBN:</strong> {book.isbn}</p>
                  <p><strong>Available Copies:</strong> {book.availableCopies}</p>
                  <p className={`availability ${book.availableCopies > 0 ? 'available' : 'unavailable'}`}>
                    {book.availableCopies > 0 ? 'Available' : 'Not Available'}
                  </p>
                </div>
              ))}
            </div>
          </div>
        )}

        {isSearching && (
          <div className="loading">
            <p>Searching for books...</p>
          </div>
        )}
      </main>

      {/* Register Modal */}
      <Register isOpen={isRegisterOpen} onClose={closeRegister} />
      
      {/* Login Modal */}
      <Login isOpen={isLoginOpen} onClose={closeLogin} />
    </div>
  );
};

export default HomePage;