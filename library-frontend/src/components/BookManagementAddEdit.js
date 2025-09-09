// src/components/BookManagementAddEdit.js
import React, { useState, useEffect } from 'react';
import './BookManagementAddEdit.css';

const BookManagementAddEdit = ({ book, isEditMode, onSave, onCancel }) => {
  const [formData, setFormData] = useState({
    isbn: '',
    title: '',
    author: '',
    category: '',
    publicationYear: '',
    totalCopies: '',
    availableCopies: '',
    publisher: '',
    description: '',
    shelfLocation: '',
    status: 'ACTIVE'
  });

  const [errors, setErrors] = useState({});

  const categories = [
    'Fiction',
    'Science',
    'Technology',
    'History',
    'Business',
    'Self-Help',
    'Biography',
    'Reference',
    'Children',
    'Academic'
  ];

  const statuses = [
    { value: 'ACTIVE', label: 'Active' },
    { value: 'INACTIVE', label: 'Inactive' },
    { value: 'DAMAGED', label: 'Damaged' },
    { value: 'LOST', label: 'Lost' }
  ];

  useEffect(() => {
    if (isEditMode && book) {
      setFormData({
        isbn: book.isbn || '',
        title: book.title || '',
        author: book.author || '',
        category: book.category || '',
        publicationYear: book.publicationYear || '',
        totalCopies: book.totalCopies || '',
        availableCopies: book.availableCopies || '',
        publisher: book.publisher || '',
        description: book.description || '',
        shelfLocation: book.shelfLocation || '',
        status: book.status || 'ACTIVE'
      });
    } else {
      // Reset form for add mode
      setFormData({
        isbn: '',
        title: '',
        author: '',
        category: '',
        publicationYear: '',
        totalCopies: '',
        availableCopies: '',
        publisher: '',
        description: '',
        shelfLocation: '',
        status: 'ACTIVE'
      });
    }
    setErrors({});
  }, [book, isEditMode]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }

    // Auto-set available copies when total copies changes (for new books)
    if (name === 'totalCopies' && !isEditMode) {
      setFormData(prev => ({
        ...prev,
        availableCopies: value
      }));
    }
  };

  const validateForm = () => {
    const newErrors = {};

    // Required fields
    if (!formData.isbn.trim()) {
      newErrors.isbn = 'ISBN is required';
    } else if (!/^978-\d-\d{3}-\d{5}-\d$/.test(formData.isbn)) {
      newErrors.isbn = 'ISBN format should be: 978-X-XXX-XXXXX-X';
    }

    if (!formData.title.trim()) {
      newErrors.title = 'Title is required';
    }

    if (!formData.author.trim()) {
      newErrors.author = 'Author is required';
    }

    if (!formData.category) {
      newErrors.category = 'Category is required';
    }

    if (!formData.publicationYear) {
      newErrors.publicationYear = 'Publication year is required';
    } else {
      const year = parseInt(formData.publicationYear);
      const currentYear = new Date().getFullYear();
      if (year < 1000 || year > currentYear) {
        newErrors.publicationYear = `Year must be between 1000 and ${currentYear}`;
      }
    }

    if (!formData.totalCopies) {
      newErrors.totalCopies = 'Total copies is required';
    } else {
      const total = parseInt(formData.totalCopies);
      if (total < 1) {
        newErrors.totalCopies = 'Total copies must be at least 1';
      }
    }

    if (!formData.availableCopies && formData.availableCopies !== '0') {
      newErrors.availableCopies = 'Available copies is required';
    } else {
      const available = parseInt(formData.availableCopies);
      const total = parseInt(formData.totalCopies);
      if (available < 0) {
        newErrors.availableCopies = 'Available copies cannot be negative';
      } else if (available > total) {
        newErrors.availableCopies = 'Available copies cannot exceed total copies';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (validateForm()) {
      const bookData = {
        ...formData,
        publicationYear: parseInt(formData.publicationYear),
        totalCopies: parseInt(formData.totalCopies),
        availableCopies: parseInt(formData.availableCopies)
      };
      onSave(bookData);
    }
  };

  const handleCancel = () => {
    onCancel();
  };

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="modal-header">
          <h2>{isEditMode ? 'Edit Book' : 'Add New Book'}</h2>
        </div>

        <form onSubmit={handleSubmit} className="book-form">
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="isbn">ISBN *</label>
              <input
                type="text"
                id="isbn"
                name="isbn"
                value={formData.isbn}
                onChange={handleInputChange}
                placeholder="978-0-123-45678-9"
                className={errors.isbn ? 'error' : ''}
                disabled={isEditMode} // ISBN should not be editable
              />
              {errors.isbn && <span className="error-text">{errors.isbn}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="title">Title *</label>
              <input
                type="text"
                id="title"
                name="title"
                value={formData.title}
                onChange={handleInputChange}
                placeholder="Enter book title"
                className={errors.title ? 'error' : ''}
              />
              {errors.title && <span className="error-text">{errors.title}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="author">Author *</label>
              <input
                type="text"
                id="author"
                name="author"
                value={formData.author}
                onChange={handleInputChange}
                placeholder="Enter author name"
                className={errors.author ? 'error' : ''}
              />
              {errors.author && <span className="error-text">{errors.author}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="category">Category *</label>
              <select
                id="category"
                name="category"
                value={formData.category}
                onChange={handleInputChange}
                className={errors.category ? 'error' : ''}
              >
                <option value="">Select Category</option>
                {categories.map((cat, index) => (
                  <option key={index} value={cat}>{cat}</option>
                ))}
              </select>
              {errors.category && <span className="error-text">{errors.category}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="publicationYear">Publication Year *</label>
              <input
                type="number"
                id="publicationYear"
                name="publicationYear"
                value={formData.publicationYear}
                onChange={handleInputChange}
                placeholder="2023"
                min="1000"
                max={new Date().getFullYear()}
                className={errors.publicationYear ? 'error' : ''}
              />
              {errors.publicationYear && <span className="error-text">{errors.publicationYear}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="publisher">Publisher</label>
              <input
                type="text"
                id="publisher"
                name="publisher"
                value={formData.publisher}
                onChange={handleInputChange}
                placeholder="Enter publisher name"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="totalCopies">Total Copies *</label>
              <input
                type="number"
                id="totalCopies"
                name="totalCopies"
                value={formData.totalCopies}
                onChange={handleInputChange}
                placeholder="1"
                min="1"
                className={errors.totalCopies ? 'error' : ''}
              />
              {errors.totalCopies && <span className="error-text">{errors.totalCopies}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="availableCopies">Available Copies *</label>
              <input
                type="number"
                id="availableCopies"
                name="availableCopies"
                value={formData.availableCopies}
                onChange={handleInputChange}
                placeholder="1"
                min="0"
                className={errors.availableCopies ? 'error' : ''}
              />
              {errors.availableCopies && <span className="error-text">{errors.availableCopies}</span>}
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="shelfLocation">Shelf Location</label>
              <input
                type="text"
                id="shelfLocation"
                name="shelfLocation"
                value={formData.shelfLocation}
                onChange={handleInputChange}
                placeholder="A1-001"
              />
            </div>

            <div className="form-group">
              <label htmlFor="status">Status *</label>
              <select
                id="status"
                name="status"
                value={formData.status}
                onChange={handleInputChange}
              >
                {statuses.map((status, index) => (
                  <option key={index} value={status.value}>{status.label}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="description">Description</label>
            <textarea
              id="description"
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="Enter book description"
              rows="3"
            />
          </div>

          <div className="form-actions">
            <button type="button" className="cancel-btn" onClick={handleCancel}>
              Cancel
            </button>
            <button type="submit" className="save-btn">
              {isEditMode ? 'Update' : 'Save'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default BookManagementAddEdit;