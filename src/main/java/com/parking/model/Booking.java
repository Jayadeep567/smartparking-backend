package com.parking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne private User user;
    @ManyToOne private Slot slot;
    private String vehicleNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double amount;
    private String status = "ACTIVE"; // ACTIVE or CANCELLED

    public Booking() {}
    public Booking(User user, Slot slot, String vehicleNumber, LocalDateTime s, LocalDateTime e, double amount) {
        this.user = user; this.slot = slot; this.vehicleNumber = vehicleNumber;
        this.startTime = s; this.endTime = e; this.amount = amount;
    }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public Slot getSlot() { return slot; }
    public String getVehicleNumber() { return vehicleNumber; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
