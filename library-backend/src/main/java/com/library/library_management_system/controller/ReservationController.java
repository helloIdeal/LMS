package com.library.library_management_system.controller;

import com.library.library_management_system.entity.Reservation;
import com.library.library_management_system.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "http://localhost:3000")
public class ReservationController {
    
    @Autowired
    private ReservationService reservationService;
    
    // Create a new reservation
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
        try {
            Reservation reservation = reservationService.createReservation(
                request.getUserId(), 
                request.getBookId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating reservation: " + e.getMessage());
        }
    }
    
    // Get all reservations (Admin only)
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get reservation by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable Long id) {
        try {
            Optional<Reservation> reservation = reservationService.findById(id);
            if (reservation.isPresent()) {
                return ResponseEntity.ok(reservation.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving reservation: " + e.getMessage());
        }
    }
    
    // Get user's reservations
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getUserReservations(@PathVariable Long userId) {
        try {
            List<Reservation> reservations = reservationService.getUserReservations(userId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get user's active reservations
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Reservation>> getUserActiveReservations(@PathVariable Long userId) {
        try {
            List<Reservation> reservations = reservationService.getUserActiveReservations(userId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get reservations for a specific book
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Reservation>> getBookReservations(@PathVariable Long bookId) {
        try {
            List<Reservation> reservations = reservationService.getBookReservations(bookId);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get reservations by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Reservation>> getReservationsByStatus(@PathVariable String status) {
        try {
            Reservation.ReservationStatus reservationStatus = 
                Reservation.ReservationStatus.valueOf(status.toUpperCase());
            List<Reservation> reservations = reservationService.getReservationsByStatus(reservationStatus);
            return ResponseEntity.ok(reservations);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Cancel a reservation
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            Reservation reservation = reservationService.cancelReservation(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error cancelling reservation: " + e.getMessage());
        }
    }
    
    // Fulfill a reservation (when user picks up the book)
    @PostMapping("/{id}/fulfill")
    public ResponseEntity<?> fulfillReservation(@PathVariable Long id) {
        try {
            Reservation reservation = reservationService.fulfillReservation(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error fulfilling reservation: " + e.getMessage());
        }
    }
    
    // Get expired reservations (Admin only)
    @GetMapping("/expired")
    public ResponseEntity<List<Reservation>> getExpiredReservations() {
        try {
            List<Reservation> reservations = reservationService.getExpiredReservations();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get reservations needing notification (Admin only)
    @GetMapping("/needs-notification")
    public ResponseEntity<List<Reservation>> getReservationsNeedingNotification() {
        try {
            List<Reservation> reservations = reservationService.getReservationsNeedingNotification();
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get reservations expiring soon
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<Reservation>> getReservationsExpiringSoon(
            @RequestParam(defaultValue = "3") int days) {
        try {
            List<Reservation> reservations = reservationService.getReservationsExpiringSoon(days);
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Update expired reservations (Admin only - scheduled task)
    @PostMapping("/update-expired")
    public ResponseEntity<?> updateExpiredReservations() {
        try {
            reservationService.updateExpiredReservations();
            return ResponseEntity.ok().body("Expired reservations updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating expired reservations: " + e.getMessage());
        }
    }
    
    // Send pending notifications (Admin only - scheduled task)
    @PostMapping("/send-notifications")
    public ResponseEntity<?> sendPendingNotifications() {
        try {
            reservationService.sendPendingNotifications();
            return ResponseEntity.ok().body("Notifications sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error sending notifications: " + e.getMessage());
        }
    }
    
    // Get reservation queue for a book
    @GetMapping("/book/{bookId}/queue")
    public ResponseEntity<?> getReservationQueue(@PathVariable Long bookId) {
        try {
            List<Reservation> queue = reservationService.getBookReservations(bookId);
            
            // Create a response with queue information
            ReservationQueueResponse response = new ReservationQueueResponse(
                bookId,
                queue.size(),
                queue
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving reservation queue: " + e.getMessage());
        }
    }
    
    // Get reservation statistics (Admin only)
    @GetMapping("/statistics")
    public ResponseEntity<?> getReservationStatistics() {
        try {
            List<Reservation> allReservations = reservationService.getAllReservations();
            List<Reservation> activeReservations = reservationService.getReservationsByStatus(Reservation.ReservationStatus.ACTIVE);
            List<Reservation> availableReservations = reservationService.getReservationsByStatus(Reservation.ReservationStatus.AVAILABLE);
            List<Reservation> expiredReservations = reservationService.getExpiredReservations();
            List<Reservation> needingNotification = reservationService.getReservationsNeedingNotification();
            
            ReservationStatistics stats = new ReservationStatistics(
                allReservations.size(),
                activeReservations.size(),
                availableReservations.size(),
                expiredReservations.size(),
                needingNotification.size()
            );
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving statistics: " + e.getMessage());
        }
    }
    
    // Check if user can reserve a book
    @GetMapping("/can-reserve")
    public ResponseEntity<?> canUserReserveBook(
            @RequestParam Long userId, 
            @RequestParam Long bookId) {
        try {
            // This is a simplified check - you might want to implement this in the service
            List<Reservation> userActiveReservations = reservationService.getUserActiveReservations(userId);
            boolean canReserve = userActiveReservations.size() < 5; // MAX_RESERVATIONS_PER_USER
            
            CanReserveResponse response = new CanReserveResponse(
                canReserve,
                canReserve ? "User can reserve this book" : "User has reached maximum reservation limit"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error checking reservation eligibility: " + e.getMessage());
        }
    }
    
    // Inner classes for request/response DTOs
    public static class ReservationRequest {
        private Long userId;
        private Long bookId;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
    }
    
    public static class ReservationQueueResponse {
        private Long bookId;
        private int queueSize;
        private List<Reservation> queue;
        
        public ReservationQueueResponse(Long bookId, int queueSize, List<Reservation> queue) {
            this.bookId = bookId;
            this.queueSize = queueSize;
            this.queue = queue;
        }
        
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public int getQueueSize() { return queueSize; }
        public void setQueueSize(int queueSize) { this.queueSize = queueSize; }
        public List<Reservation> getQueue() { return queue; }
        public void setQueue(List<Reservation> queue) { this.queue = queue; }
    }
    
    public static class ReservationStatistics {
        private int totalReservations;
        private int activeReservations;
        private int availableForPickup;
        private int expiredReservations;
        private int needingNotification;
        
        public ReservationStatistics(int totalReservations, int activeReservations, 
                                   int availableForPickup, int expiredReservations, int needingNotification) {
            this.totalReservations = totalReservations;
            this.activeReservations = activeReservations;
            this.availableForPickup = availableForPickup;
            this.expiredReservations = expiredReservations;
            this.needingNotification = needingNotification;
        }
        
        public int getTotalReservations() { return totalReservations; }
        public void setTotalReservations(int totalReservations) { this.totalReservations = totalReservations; }
        public int getActiveReservations() { return activeReservations; }
        public void setActiveReservations(int activeReservations) { this.activeReservations = activeReservations; }
        public int getAvailableForPickup() { return availableForPickup; }
        public void setAvailableForPickup(int availableForPickup) { this.availableForPickup = availableForPickup; }
        public int getExpiredReservations() { return expiredReservations; }
        public void setExpiredReservations(int expiredReservations) { this.expiredReservations = expiredReservations; }
        public int getNeedingNotification() { return needingNotification; }
        public void setNeedingNotification(int needingNotification) { this.needingNotification = needingNotification; }
    }
    
    public static class CanReserveResponse {
        private boolean canReserve;
        private String message;
        
        public CanReserveResponse(boolean canReserve, String message) {
            this.canReserve = canReserve;
            this.message = message;
        }
        
        public boolean isCanReserve() { return canReserve; }
        public void setCanReserve(boolean canReserve) { this.canReserve = canReserve; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}