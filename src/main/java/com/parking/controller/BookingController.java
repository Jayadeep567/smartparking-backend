package com.parking.controller;

import com.parking.model.Booking;
import com.parking.repo.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private static final double RATE_PER_HOUR = 20.0;
    private final BookingRepository bookings; private final SlotRepository slots; private final UserRepository users;
    public BookingController(BookingRepository b, SlotRepository s, UserRepository u) { bookings = b; slots = s; users = u; }

    record BookReq(@NotNull Long slotId, @NotBlank String vehicleNumber,
                   @NotNull LocalDateTime startTime, @NotNull LocalDateTime endTime) {}

    @PostMapping
    public ResponseEntity<?> book(@Valid @RequestBody BookReq r, Principal p) {
        if (!r.endTime().isAfter(r.startTime()))
            return ResponseEntity.badRequest().body(Map.of("message", "End time must be after start time"));
        if (r.startTime().isBefore(LocalDateTime.now().minusMinutes(5)))
            return ResponseEntity.badRequest().body(Map.of("message", "Start time cannot be in the past"));
        var slot = slots.findById(r.slotId());
        if (slot.isEmpty()) return ResponseEntity.badRequest().body(Map.of("message", "Slot not found"));
        if (bookings.isOverlapping(r.slotId(), r.startTime(), r.endTime()))
            return ResponseEntity.status(409).body(Map.of("message", "Slot already booked for that time"));
        long mins = Duration.between(r.startTime(), r.endTime()).toMinutes();
        double amount = Math.ceil(mins / 60.0) * RATE_PER_HOUR;
        var user = users.findByEmail(p.getName()).orElseThrow();
        return ResponseEntity.ok(bookings.save(new Booking(user, slot.get(), r.vehicleNumber().toUpperCase(),
                r.startTime(), r.endTime(), amount)));
    }

    @GetMapping("/my")
    public List<Booking> mine(Principal p) { return bookings.findByUserEmailOrderByStartTimeDesc(p.getName()); }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id, Principal p) {
        var b = bookings.findById(id);
        if (b.isEmpty() || !b.get().getUser().getEmail().equals(p.getName()))
            return ResponseEntity.status(404).body(Map.of("message", "Booking not found"));
        b.get().setStatus("CANCELLED");
        bookings.save(b.get());
        return ResponseEntity.ok(Map.of("message", "Booking cancelled"));
    }
}
