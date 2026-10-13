package com.parking.controller;

import com.parking.repo.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/slots")
public class SlotController {
    private final SlotRepository slots; private final BookingRepository bookings;
    public SlotController(SlotRepository s, BookingRepository b) { slots = s; bookings = b; }

    // Lists all slots and whether each is free for the requested time window
    @GetMapping
    public List<Map<String, Object>> list(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Map<String, Object>> out = new ArrayList<>();
        slots.findAll().forEach(s -> out.add(Map.of("id", s.getId(), "slotNumber", s.getSlotNumber(),
                "available", !bookings.isOverlapping(s.getId(), start, end))));
        return out;
    }
}
