package com.parking.model;

import jakarta.persistence.*;

@Entity
public class Slot {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true) private String slotNumber;

    public Slot() {}
    public Slot(String slotNumber) { this.slotNumber = slotNumber; }
    public Long getId() { return id; }
    public String getSlotNumber() { return slotNumber; }
}
