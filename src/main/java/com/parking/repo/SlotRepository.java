package com.parking.repo;

import com.parking.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotRepository extends JpaRepository<Slot, Long> {
    boolean existsBySlotNumber(String slotNumber);
}
