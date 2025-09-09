// src/components/MemberDashboard.js
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import ProfileModal from './ProfileModal';
import './MemberDashboard.css';

const MemberDashboard = () => {
  const [user, setUser] = useState(null);
  const [activeBorrowings, setActiveBorrowings] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showProfileModal, setShowProfileModal] = useState(false);
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
    fetchUserData(parsedUser.id);
  }, [navigate]);

  const fetchUserData = async (userId) => {
    try {
      setLoading(true);
      
      // Fetch active borrowings
      const borrowingsResponse = await axios.get(`http://localhost:8080/api/transactions/user/${userId}/active`);
      setActiveBorrowings(borrowingsResponse.data);

      // Note: Reservations endpoint would be similar - placeholder for now
      setReservations([]);
      
    } catch (err) {
      console.error('Error fetching user data:', err);
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('user');
    localStorage.removeItem('userRole');
    localStorage.removeItem('userId');
    navigate('/');
  };

  const handleBorrowReserve = () => {
    navigate('/book-search');
  };

  const handlePayFines = () => {
    alert('Pay Fines functionality - placeholder for future implementation');
  };

  const handleProfileManagement = () => {
    setShowProfileModal(true);
  };

  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  const isOverdue = (dueDate) => {
    return new Date(dueDate) < new Date();
  };

  const calculateFine = (transaction) => {
    if (!isOverdue(transaction.dueDate)) return 0;
    return transaction.fineAmount || 0;
  };

  if (loading) {
    return (
      <div className="member-dashboard">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading dashboard...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="member-dashboard">
        <div className="error-container">
          <h3>Error</h3>
          <p>{error}</p>
          <button onClick={() => window.location.reload()} className="retry-btn">
            Retry
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="member-dashboard">
      {/* Header */}
      <div className="dashboard-header">
        <div className="header-left">
          <h1>Member Dashboard</h1>
          <p>Welcome back, {user?.fullName || user?.username}!</p>
        </div>
        <div className="header-right">
          <button onClick={handleLogout} className="logout-btn">
            Logout
          </button>
        </div>
      </div>

      {/* Profile Information */}
      <div className="dashboard-section">
        <h2>Profile Information</h2>
        <div className="profile-table-container">
          <table className="profile-table">
            <tbody>
              <tr>
                <td><strong>Full Name:</strong></td>
                <td>{user?.fullName || 'Not provided'}</td>
              </tr>
              <tr>
                <td><strong>Username:</strong></td>
                <td>{user?.username}</td>
              </tr>
              <tr>
                <td><strong>Email:</strong></td>
                <td>{user?.email || 'Not provided'}</td>
              </tr>
              <tr>
                <td><strong>Phone:</strong></td>
                <td>{user?.phone || 'Not provided'}</td>
              </tr>
              <tr>
                <td><strong>Membership Type:</strong></td>
                <td>{user?.membershipType || 'Standard'}</td>
              </tr>
              <tr>
                <td><strong>Membership Expires:</strong></td>
                <td>
                  {user?.membershipEndDate 
                    ? formatDate(user.membershipEndDate)
                    : 'Not available'
                  }
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      {/* Borrowing Information */}
      <div className="dashboard-section">
        <h2>Current Borrowings ({activeBorrowings.length}/3)</h2>
        <div className="borrowings-table-container">
          {activeBorrowings.length === 0 ? (
            <div className="no-data">
              <p>No books currently borrowed</p>
            </div>
          ) : (
            <table className="borrowings-table">
              <thead>
                <tr>
                  <th>Book Title</th>
                  <th>Author</th>
                  <th>Borrow Date</th>
                  <th>Due Date</th>
                  <th>Status</th>
                  <th>Fine</th>
                </tr>
              </thead>
              <tbody>
                {activeBorrowings.map((transaction) => (
                  <tr key={transaction.id} className={isOverdue(transaction.dueDate) ? 'overdue-row' : ''}>
                    <td>{transaction.book?.title || 'Unknown'}</td>
                    <td>{transaction.book?.author || 'Unknown'}</td>
                    <td>{formatDate(transaction.borrowDate)}</td>
                    <td>{formatDate(transaction.dueDate)}</td>
                    <td>
                      <span className={`status-badge ${transaction.status.toLowerCase()}`}>
                        {transaction.status}
                      </span>
                    </td>
                    <td>
                      {calculateFine(transaction) > 0 
                        ? `$${calculateFine(transaction).toFixed(2)}`
                        : '-'
                      }
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* Reservations */}
      <div className="dashboard-section">
        <h2>Active Reservations ({reservations.length})</h2>
        <div className="reservations-table-container">
          {reservations.length === 0 ? (
            <div className="no-data">
              <p>No active reservations</p>
            </div>
          ) : (
            <table className="reservations-table">
              <thead>
                <tr>
                  <th>Book Title</th>
                  <th>Author</th>
                  <th>Reserved Date</th>
                  <th>Queue Position</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {reservations.map((reservation) => (
                  <tr key={reservation.id}>
                    <td>{reservation.book?.title || 'Unknown'}</td>
                    <td>{reservation.book?.author || 'Unknown'}</td>
                    <td>{formatDate(reservation.reservationDate)}</td>
                    <td>{reservation.queuePosition || 'N/A'}</td>
                    <td>
                      <span className={`status-badge ${reservation.status.toLowerCase()}`}>
                        {reservation.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {/* Action Buttons */}
      <div className="dashboard-actions">
        <button 
          onClick={handleBorrowReserve} 
          className="action-btn primary-btn"
        >
          Borrow / Reserve Books
        </button>
        
        <button 
          onClick={handlePayFines} 
          className="action-btn secondary-btn"
          disabled={activeBorrowings.every(t => calculateFine(t) === 0)}
        >
          Pay Fines
        </button>
        
        <button 
          onClick={handleProfileManagement} 
          className="action-btn tertiary-btn"
        >
          Profile Management
        </button>
      </div>

      {/* Profile Modal */}
      {showProfileModal && (
        <ProfileModal
          isOpen={showProfileModal}
          onClose={() => setShowProfileModal(false)}
          user={user}
          onUserUpdate={(updatedUser) => {
            setUser(updatedUser);
            localStorage.setItem('user', JSON.stringify(updatedUser));
          }}
        />
      )}
    </div>
  );
};

export default MemberDashboard;