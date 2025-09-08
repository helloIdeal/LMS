
// ReservationRepository.java
package com.library.library_management_system.repository;

import com.library.library_management_system.entity.Reservation;
import com.library.library_management_system.entity.User;
import com.library.library_management_system.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // Find reservations by user
    List<Reservation> findByUser(User user);
    
    // Find reservations by book
    List<Reservation> findByBook(Book book);
    
    // Find reservations by status
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    
    // Find active reservations by user
    @Query("SELECT r FROM Reservation r WHERE r.user = :user AND r.status = 'ACTIVE'")
    List<Reservation> findActiveReservationsByUser(@Param("user") User user);
    
    // Find active reservations for a book (ordered by queue position)
    @Query("SELECT r FROM Reservation r WHERE r.book = :book AND r.status = 'ACTIVE' ORDER BY r.queuePosition ASC")
    List<Reservation> findActiveReservationsByBook(@Param("book") Book book);
    
    // Count active reservations by user
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user = :user AND r.status = 'ACTIVE'")
    Long countActiveReservationsByUser(@Param("user") User user);
    
    // Find expired reservations
    @Query("SELECT r FROM Reservation r WHERE r.expiryDate < :currentDate AND (r.status = 'ACTIVE' OR r.status = 'AVAILABLE')")
    List<Reservation> findExpiredReservations(@Param("currentDate") LocalDate currentDate);
    
    // Find reservations that need notification (book became available)
    @Query("SELECT r FROM Reservation r WHERE r.status = 'AVAILABLE' AND r.notificationSent = false")
    List<Reservation> findReservationsNeedingNotification();
    
    // Find next reservation in queue for a book
    @Query("SELECT r FROM Reservation r WHERE r.book = :book AND r.status = 'ACTIVE' ORDER BY r.queuePosition ASC LIMIT 1")
    Reservation findNextReservationInQueue(@Param("book") Book book);
    
    // Check if user has already reserved a book
    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.user = :user AND r.book = :book AND r.status = 'ACTIVE'")
    boolean hasUserReservedBook(@Param("user") User user, @Param("book") Book book);
    
    // Find reservations expiring soon
    @Query("SELECT r FROM Reservation r WHERE r.expiryDate BETWEEN :startDate AND :endDate AND (r.status = 'ACTIVE' OR r.status = 'AVAILABLE')")
    List<Reservation> findReservationsExpiringSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Get queue position for a specific reservation
    @Query("SELECT COUNT(r) + 1 FROM Reservation r WHERE r.book = :book AND r.status = 'ACTIVE' AND r.reservationDate < :reservationDate")
    Integer calculateQueuePosition(@Param("book") Book book, @Param("reservationDate") LocalDate reservationDate);
}