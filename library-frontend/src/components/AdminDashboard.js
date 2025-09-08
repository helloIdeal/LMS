// src/components/AdminDashboard.js
import React from 'react';
import { useNavigate } from 'react-router-dom';
import './AdminDashboard.css';

const AdminDashboard = () => {
  const navigate = useNavigate();

  const handleLogout = () => {
    // Clear user data from localStorage
    localStorage.removeItem('user');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    
    // Navigate back to home page
    navigate('/');
  };

  const handleBookManagement = () => {
    // TODO: Navigate to book management page
    console.log('Navigate to Book Management');
  };

  const handleLendingManagement = () => {
    // TODO: Navigate to lending management page
    console.log('Navigate to Lending Management');
  };

  const handleMemberManagement = () => {
    // TODO: Navigate to member management page
    console.log('Navigate to Member Management');
  };

  const handleOverdueFines = () => {
    // TODO: Navigate to overdue/fines page
    console.log('Navigate to Overdue/Fines');
  };

  const handleHome = () => {
    navigate('/');
  };

  return (
    <div className="admin-dashboard">
      {/* Video Background */}
      <div className="video-background">
        <video autoPlay muted loop playsInline className="video-bg">
          <source src="/library-bg.mp4" type="video/mp4" />
          Your browser does not support the video tag.
        </video>
      </div>
      
      <div className="overlay"></div>

      {/* Navigation Bar */}
      <nav className="admin-navbar">
        <div className="nav-left">
          <button className="nav-btn home-btn" onClick={handleHome}>
            Home
          </button>
        </div>
        <div className="nav-right">
          <button className="nav-btn logout-btn" onClick={handleLogout}>
            Logout
          </button>
        </div>
      </nav>

      {/* Main Dashboard Content */}
      <main className="dashboard-main">
        {/* Dashboard Header */}
        <div className="dashboard-header">
          <h1>ADMIN DASHBOARD</h1>
        </div>

        {/* Dashboard Grid */}
        <div className="dashboard-grid">
          <button 
            className="dashboard-card book-management"
            onClick={handleBookManagement}
          >
            <span className="card-text">Book Management</span>
          </button>

          <button 
            className="dashboard-card lending-management"
            onClick={handleLendingManagement}
          >
            <span className="card-text">Lending Management</span>
          </button>

          <button 
            className="dashboard-card member-management"
            onClick={handleMemberManagement}
          >
            <span className="card-text">Member Management</span>
          </button>

          <button 
            className="dashboard-card overdue-fines"
            onClick={handleOverdueFines}
          >
            <span className="card-text">Overdue/Fines</span>
          </button>
        </div>
      </main>
    </div>
  );
};

export default AdminDashboard;