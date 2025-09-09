// src/App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './components/HomePage';
import AdminDashboard from './components/AdminDashboard';
import BookManagement from './components/BookManagement';
import MemberManagement from './components/MemberManagement';
import MemberDashboard from './components/MemberDashboard';
import BookSearch from './components/BookSearch';

function App() {
  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/admin-dashboard" element={<AdminDashboard />} />
          <Route path="/book-management" element={<BookManagement />} />
          <Route path="/member-management" element={<MemberManagement />} />
          <Route path="/member-dashboard" element={<MemberDashboard />} />
          <Route path="/book-search" element={<BookSearch />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;