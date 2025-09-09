// src/components/ProfileModal.js
import React, { useState } from 'react';
import axios from 'axios';
import './ProfileModal.css';

const ProfileModal = ({ isOpen, onClose, user, onUserUpdate }) => {
  const [formData, setFormData] = useState({
    fullName: user?.fullName || '',
    username: user?.username || '',
    email: user?.email || '',
    phone: user?.phone || '',
    password: '',
    confirmPassword: '',
    address: user?.address || '',
    membershipType: user?.membershipType || 'STANDARD',
    membershipEndDate: user?.membershipEndDate || ''
  });
  
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Clear messages when user starts typing
    if (error) setError('');
    if (success) setSuccess('');
  };

  const validateForm = () => {
    // Email validation
    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      setError('Please enter a valid email address');
      return false;
    }

    // Password validation (only if password is being changed)
    if (formData.password) {
      if (formData.password.length < 6) {
        setError('Password must be at least 6 characters long');
        return false;
      }
      
      if (formData.password !== formData.confirmPassword) {
        setError('Passwords do not match');
        return false;
      }
    }

    // Phone validation (optional, but if provided should be valid)
    if (formData.phone && !/^[\d\s\-+()]+$/.test(formData.phone)) {
      setError('Please enter a valid phone number');
      return false;
    }

    return true;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    setShowConfirmation(true);
  };

  const handleConfirmSave = async () => {
    setIsLoading(true);
    setShowConfirmation(false);
    setError('');
    setSuccess('');

    try {
      // Prepare update data - only include editable fields: email, password, address, phone
      const updateData = {
        email: formData.email,
        phone: formData.phone,
        address: formData.address
      };

      // Add password only if it's being changed
      if (formData.password) {
        updateData.password = formData.password;
      }

      const response = await axios.put(
        `http://localhost:8080/api/users/${user.id}`,
        updateData
      );

      // Update local user data
      const updatedUser = { ...user, ...response.data };
      onUserUpdate(updatedUser);
      
      setSuccess('Profile updated successfully!');
      
      // Clear password fields after successful update
      setFormData(prev => ({
        ...prev,
        password: '',
        confirmPassword: ''
      }));

      // Close modal after 2 seconds
      setTimeout(() => {
        onClose();
      }, 2000);

    } catch (err) {
      console.error('Profile update error:', err);
      if (err.response?.data) {
        setError(err.response.data);
      } else {
        setError('Failed to update profile. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleBackdropClick = (e) => {
    if (e.target === e.currentTarget && !showConfirmation) {
      onClose();
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Not available';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  if (!isOpen) return null;

  return (
    <div className="modal-backdrop" onClick={handleBackdropClick}>
      <div className="profile-modal">
        <div className="modal-header">
          <h2>Profile Management</h2>
          <button 
            className="close-btn" 
            onClick={onClose}
            disabled={isLoading}
          >
            ×
          </button>
        </div>

        <div className="modal-body">
          <form onSubmit={handleSubmit}>
            {/* Read-only fields */}
            <div className="form-section">
              <h3>Account Information (Read-only)</h3>
              
              <div className="form-row">
                <div className="form-group readonly">
                  <label>Username:</label>
                  <input
                    type="text"
                    value={formData.username}
                    readOnly
                    className="readonly-input"
                  />
                </div>
                
                <div className="form-group readonly">
                  <label>Membership Type:</label>
                  <input
                    type="text"
                    value={formData.membershipType}
                    readOnly
                    className="readonly-input"
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group readonly">
                  <label>Full Name:</label>
                  <input
                    type="text"
                    value={formData.fullName}
                    readOnly
                    className="readonly-input"
                  />
                </div>

                <div className="form-group readonly">
                  <label>Membership Expires:</label>
                  <input
                    type="text"
                    value={formatDate(formData.membershipEndDate)}
                    readOnly
                    className="readonly-input"
                  />
                </div>
              </div>
            </div>

            {/* Editable fields */}
            <div className="form-section">
              <h3>Personal Information (Editable)</h3>
              
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="email">Email: *</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="Enter your email"
                    required
                    disabled={isLoading}
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="phone">Phone:</label>
                  <input
                    type="tel"
                    id="phone"
                    name="phone"
                    value={formData.phone}
                    onChange={handleChange}
                    placeholder="Enter your phone number"
                    disabled={isLoading}
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="address">Address:</label>
                <textarea
                  id="address"
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                  placeholder="Enter your address"
                  rows="3"
                  disabled={isLoading}
                />
              </div>
            </div>

            {/* Password change section */}
            <div className="form-section">
              <h3>Change Password (Optional)</h3>
              <p className="password-note">Leave blank to keep current password</p>
              
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="password">New Password:</label>
                  <input
                    type="password"
                    id="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="Enter new password"
                    disabled={isLoading}
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="confirmPassword">Confirm Password:</label>
                  <input
                    type="password"
                    id="confirmPassword"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    placeholder="Confirm new password"
                    disabled={isLoading}
                  />
                </div>
              </div>
            </div>

            {/* Messages */}
            {error && (
              <div className="error-message">
                {error}
              </div>
            )}

            {success && (
              <div className="success-message">
                {success}
              </div>
            )}

            {/* Action buttons */}
            <div className="form-actions">
              <button 
                type="submit" 
                className="save-btn"
                disabled={isLoading}
              >
                {isLoading ? 'Saving...' : 'Save Changes'}
              </button>
              <button 
                type="button" 
                className="cancel-btn"
                onClick={onClose}
                disabled={isLoading}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>

        {/* Confirmation Dialog */}
        {showConfirmation && (
          <div className="confirmation-overlay">
            <div className="confirmation-dialog">
              <h3>Confirm Changes</h3>
              <p>Are you sure you want to save these changes to your profile?</p>
              <div className="confirmation-actions">
                <button 
                  className="confirm-btn"
                  onClick={handleConfirmSave}
                >
                  Yes, Save Changes
                </button>
                <button 
                  className="cancel-confirm-btn"
                  onClick={() => setShowConfirmation(false)}
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfileModal;