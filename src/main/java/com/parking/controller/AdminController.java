package com.parking.controller;

import com.parking.model.*;
import com.parking.repo.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final UserRepository users; private final BookingRepository bookings; private final SlotRepository slots;
    public AdminController(UserRepository u, BookingRepository b, SlotRepository s) { users = u; bookings = b; slots = s; }

    @GetMapping("/users")
    public List<User> allUsers() { return users.findAll(); }

    @GetMapping("/bookings")
    public List<Booking> allBookings() { return bookings.findAllByOrderByStartTimeDesc(); }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        var all = bookings.findAll();
        double revenue = all.stream().filter(b -> b.getStatus().equals("ACTIVE")).mapToDouble(Booking::getAmount).sum();
        return Map.of("users", users.count(), "slots", slots.count(), "bookings", all.size(), "revenue", revenue);
    }

    @PostMapping("/slots")
    public ResponseEntity<?> addSlot(@RequestBody Map<String, String> body) {
        String n = body.getOrDefault("slotNumber", "").trim();
        if (n.isEmpty() || slots.existsBySlotNumber(n))
            return ResponseEntity.badRequest().body(Map.of("message", "Slot number missing or already exists"));
        return ResponseEntity.ok(slots.save(new Slot(n)));
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id) {
        return bookings.findById(id).map(b -> { b.setStatus("CANCELLED"); bookings.save(b);
            return ResponseEntity.ok(Map.of("message", "Cancelled")); })
            .orElse(ResponseEntity.status(404).body(Map.of("message", "Not found")));
    }
}
