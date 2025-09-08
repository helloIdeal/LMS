package com.library.library_management_system.service;

import com.library.library_management_system.entity.Reservation;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.entity.Book;
import com.library.library_management_system.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookService bookService;
    
    private static final int MAX_RESERVATIONS_PER_USER = 5;
    
    // Create a reservation
    public Reservation createReservation(Long userId, Long bookId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        // Check if user membership is valid
        if (!userService.isMembershipValid(user)) {
            throw new RuntimeException("User membership has expired");
        }
        
        // Check if user has reached reservation limit
        Long activeReservations = reservationRepository.countActiveReservationsByUser(user);
        if (activeReservations >= MAX_RESERVATIONS_PER_USER) {
            throw new RuntimeException("User has reached maximum reservation limit");
        }
        
        // Check if user has already reserved this book
        if (reservationRepository.hasUserReservedBook(user, book)) {
            throw new RuntimeException("User has already reserved this book");
        }
        
        // Check if book is currently available (shouldn't reserve available books)
        if (bookService.isBookAvailable(bookId)) {
            throw new RuntimeException("Book is currently available. Please borrow it directly.");
        }
        
        // Create reservation
        LocalDate reservationDate = LocalDate.now();
        Reservation reservation = new Reservation(user, book, reservationDate);
        
        // Calculate queue position
        Integer queuePosition = reservationRepository.calculateQueuePosition(book, reservationDate);
        reservation.setQueuePosition(queuePosition);
        
        return reservationRepository.save(reservation);
    }
    
    // Cancel a reservation
    public Reservation cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        reservation.cancel();
        
        // Update queue positions for remaining reservations
        updateQueuePositionsAfterCancellation(reservation.getBook(), reservation.getQueuePosition());
        
        return reservationRepository.save(reservation);
    }
    
    // Fulfill a reservation (when user picks up the book)
    public Reservation fulfillReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        if (reservation.getStatus() != Reservation.ReservationStatus.AVAILABLE) {
            throw new RuntimeException("Reservation is not available for pickup");
        }
        
        reservation.markAsFulfilled();
        return reservationRepository.save(reservation);
    }
    
    // Get user's reservations
    public List<Reservation> getUserReservations(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return reservationRepository.findByUser(user);
    }
    
    // Get user's active reservations
    public List<Reservation> getUserActiveReservations(Long userId) {
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return reservationRepository.findActiveReservationsByUser(user);
    }
    
    // Get all reservations
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    // Get reservations for a specific book
    public List<Reservation> getBookReservations(Long bookId) {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        return reservationRepository.findActiveReservationsByBook(book);
    }
    
    // Process book return and notify next reservation
    public void processBookReturn(Long bookId) {
        Book book = bookService.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        // Find next reservation in queue
        Reservation nextReservation = reservationRepository.findNextReservationInQueue(book);
        
        if (nextReservation != null) {
            // Mark reservation as available for pickup
            nextReservation.markAsAvailable();
            reservationRepository.save(nextReservation);
            
            // Send notification (you can implement email/SMS service here)
            sendNotification(nextReservation);
        }
    }
    
    // Send notification to user
    private void sendNotification(Reservation reservation) {
        reservation.sendNotification();
        reservationRepository.save(reservation);
        
        // TODO: Implement actual notification service (email/SMS)
        System.out.println("Notification sent to " + reservation.getUser().getEmail() + 
                          " for book: " + reservation.getBook().getTitle());
    }
    
    // Get expired reservations
    public List<Reservation> getExpiredReservations() {
        return reservationRepository.findExpiredReservations(LocalDate.now());
    }
    
    // Get reservations needing notification
    public List<Reservation> getReservationsNeedingNotification() {
        return reservationRepository.findReservationsNeedingNotification();
    }
    
    // Update expired reservations
    public void updateExpiredReservations() {
        List<Reservation> expiredReservations = getExpiredReservations();
        
        for (Reservation reservation : expiredReservations) {
            reservation.expire();
            reservationRepository.save(reservation);
            
            // Update queue positions for remaining reservations
            updateQueuePositionsAfterCancellation(reservation.getBook(), reservation.getQueuePosition());
        }
    }
    
    // Update queue positions after a cancellation or expiry
    private void updateQueuePositionsAfterCancellation(Book book, Integer removedPosition) {
        List<Reservation> remainingReservations = reservationRepository.findActiveReservationsByBook(book);
        
        for (Reservation reservation : remainingReservations) {
            if (reservation.getQueuePosition() > removedPosition) {
                reservation.setQueuePosition(reservation.getQueuePosition() - 1);
                reservationRepository.save(reservation);
            }
        }
    }
    
    // Send notifications for available reservations
    public void sendPendingNotifications() {
        List<Reservation> reservationsNeedingNotification = getReservationsNeedingNotification();
        
        for (Reservation reservation : reservationsNeedingNotification) {
            sendNotification(reservation);
        }
    }
    
    // Get reservations expiring soon
    public List<Reservation> getReservationsExpiringSoon(int days) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        return reservationRepository.findReservationsExpiringSoon(today, endDate);
    }
    
    // Find reservation by ID
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }
    
    // Get reservations by status
    public List<Reservation> getReservationsByStatus(Reservation.ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }
}