// src/components/MemberManagement.js
import React, { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import MemberManagementAddEdit from './MemberManagementAddEdit';
import VideoBackground from './VideoBackground';
import './MemberManagement.css';

const MemberManagement = () => {
  const navigate = useNavigate();
  const [members, setMembers] = useState([]);
  const [filteredMembers, setFilteredMembers] = useState([]);
  const [selectedMember, setSelectedMember] = useState(null);
  const [showAddEditModal, setShowAddEditModal] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Pagination state
  const [currentPage, setCurrentPage] = useState(1);
  const [membersPerPage] = useState(5);

  // Search state
  const [searchTerm, setSearchTerm] = useState('');

  // Calculate pagination
  const indexOfLastMember = currentPage * membersPerPage;
  const indexOfFirstMember = indexOfLastMember - membersPerPage;
  const currentMembers = filteredMembers.slice(indexOfFirstMember, indexOfLastMember);
  const totalPages = Math.ceil(filteredMembers.length / membersPerPage);

  const API_BASE_URL = 'http://localhost:8080/api';

  const fetchMembers = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_BASE_URL}/users/members`);
      
      let memberData = response.data;
      
      // If the response is a string, parse it as JSON
      if (typeof response.data === 'string') {
        try {
          memberData = JSON.parse(response.data);
        } catch (parseError) {
          console.error('Failed to parse JSON:', parseError);
          setError('Failed to parse member data');
          return;
        }
      }
      
      console.log('Members fetched:', memberData);
      setMembers(memberData);
      setError('');
    } catch (err) {
      setError('Failed to fetch members');
      console.error('Error fetching members:', err);
    } finally {
      setLoading(false);
    }
  };

  const filterMembers = useCallback(() => {
    let filtered = [...members];

    // Filter by search term (name, username, ID, email)
    if (searchTerm.trim()) {
      filtered = filtered.filter(member =>
        (member.fullName && member.fullName.toLowerCase().includes(searchTerm.toLowerCase())) ||
        (member.username && member.username.toLowerCase().includes(searchTerm.toLowerCase())) ||
        (member.id && member.id.toString().includes(searchTerm)) ||
        (member.email && member.email.toLowerCase().includes(searchTerm.toLowerCase()))
      );
    }

    console.log('Filtered members:', filtered);
    setFilteredMembers(filtered);
    setCurrentPage(1); // Reset to first page when filtering
  }, [members, searchTerm]);

  useEffect(() => {
    fetchMembers();
  }, []);

  useEffect(() => {
    filterMembers();
  }, [filterMembers]);

  const handleRowClick = (member) => {
    setSelectedMember(selectedMember?.id === member.id ? null : member);
  };

  const handleAddMember = () => {
    setSelectedMember(null);
    setIsEditMode(false);
    setShowAddEditModal(true);
  };

  const handleUpdateMember = () => {
    if (!selectedMember) {
      alert('Please select a member to update');
      return;
    }
    setIsEditMode(true);
    setShowAddEditModal(true);
  };

  const handleDeleteMember = async () => {
    if (!selectedMember) {
      alert('Please select a member to delete');
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete member "${selectedMember.fullName}" (${selectedMember.username})? This action cannot be undone.`
    );

    if (confirmed) {
      try {
        await axios.delete(`${API_BASE_URL}/users/${selectedMember.id}`);
        setMembers(members.filter(member => member.id !== selectedMember.id));
        setSelectedMember(null);
        alert('Member deleted successfully');
      } catch (err) {
        alert('Failed to delete member');
        console.error('Error deleting member:', err);
      }
    }
  };

  const handleSaveMember = async (memberData) => {
    try {
      if (isEditMode) {
        const response = await axios.put(`${API_BASE_URL}/users/${selectedMember.id}`, memberData);
        setMembers(members.map(member => 
          member.id === selectedMember.id ? response.data : member
        ));
        alert('Member updated successfully');
      } else {
        // For new member, ensure role is set to MEMBER
        const newMemberData = { ...memberData, role: 'MEMBER' };
        const response = await axios.post(`${API_BASE_URL}/users/register`, newMemberData);
        setMembers([...members, response.data]);
        alert('Member added successfully');
      }
      setShowAddEditModal(false);
      setSelectedMember(null);
    } catch (err) {
      alert(err.response?.data || 'Failed to save member');
      console.error('Error saving member:', err);
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

  const getMembershipBadge = (membershipType) => {
    const membershipClass = {
      'STANDARD': 'membership-standard',
      'PREMIUM': 'membership-premium',
      'STUDENT': 'membership-student'
    };
    return <span className={`membership-badge ${membershipClass[membershipType] || 'membership-standard'}`}>
      {membershipType || 'STANDARD'}
    </span>;
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString();
  };

  const isMembershipExpired = (endDate) => {
    if (!endDate) return false;
    return new Date(endDate) < new Date();
  };

  if (loading) {
    return <div className="loading">Loading members...</div>;
  }

  console.log('Current members to display:', currentMembers);

  return (
    <div className="member-management">
      {/* Video Background Component */}
      <VideoBackground />

      {/* Navigation Bar */}
      <nav className="member-navbar">
        <div className="nav-left">
          <button className="nav-btn home-btn" onClick={handleHome}>Home</button>
          <button className="nav-btn admin-btn" onClick={handleAdminDashboard}>Admin Dashboard</button>
        </div>
        <div className="nav-right">
          <button className="nav-btn logout-btn" onClick={handleLogout}>Logout</button>
        </div>
      </nav>

      {/* Main Content */}
      <main className="member-main">
        <div className="member-header">
          <h1>MEMBER MANAGEMENT - LIST</h1>
        </div>

        {error && <div className="error-message">{error}</div>}

        {/* Search Section */}
        <div className="search-filter-section">
          <div className="search-controls">
            <input
              type="text"
              placeholder="Search by Name, Username, ID, or Email..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
          </div>
        </div>

        {/* Members Table */}
        <div className="members-table-container">
          <table className="members-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Username</th>
                <th>Full Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Membership Type</th>
                <th>Membership End</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {currentMembers.length === 0 ? (
                <tr>
                  <td colSpan="8" className="no-members">
                    {members.length === 0 ? 'No members found' : 'No members match your search criteria'}
                  </td>
                </tr>
              ) : (
                currentMembers.map((member) => (
                  <tr
                    key={`member-${member.id}`}
                    className={`${selectedMember?.id === member.id ? 'selected-row' : ''} ${
                      isMembershipExpired(member.membershipEndDate) ? 'expired-membership' : ''
                    }`}
                    onClick={() => handleRowClick(member)}
                  >
                    <td>{member.id || 'N/A'}</td>
                    <td>{member.username || 'N/A'}</td>
                    <td>{member.fullName || 'N/A'}</td>
                    <td>{member.email || 'N/A'}</td>
                    <td>{member.phone || 'N/A'}</td>
                    <td>{getMembershipBadge(member.membershipType)}</td>
                    <td className={isMembershipExpired(member.membershipEndDate) ? 'expired-date' : ''}>
                      {formatDate(member.membershipEndDate)}
                    </td>
                    <td>
                      {isMembershipExpired(member.membershipEndDate) ? 
                        <span className="status-badge status-expired">Expired</span> : 
                        <span className="status-badge status-active">Active</span>
                      }
                    </td>
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
          <button className="action-btn add-btn" onClick={handleAddMember}>
            Add New Member
          </button>
          <button 
            className="action-btn update-btn" 
            onClick={handleUpdateMember}
            disabled={!selectedMember}
          >
            Update Member
          </button>
          <button 
            className="action-btn delete-btn" 
            onClick={handleDeleteMember}
            disabled={!selectedMember}
          >
            Delete Member
          </button>
        </div>

        {/* Results Info */}
        <div className="results-info">
          Showing {currentMembers.length > 0 ? indexOfFirstMember + 1 : 0} to {Math.min(indexOfLastMember, filteredMembers.length)} of {filteredMembers.length} members
        </div>
      </main>

      {/* Add/Edit Modal */}
      {showAddEditModal && (
        <MemberManagementAddEdit
          member={isEditMode ? selectedMember : null}
          isEditMode={isEditMode}
          onSave={handleSaveMember}
          onCancel={() => setShowAddEditModal(false)}
        />
      )}
    </div>
  );
};

export default MemberManagement;