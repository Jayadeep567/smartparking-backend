package com.parking.repo;

import com.parking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserEmailOrderByStartTimeDesc(String email);
    List<Booking> findAllByOrderByStartTimeDesc();

    @Query("select count(b) > 0 from Booking b where b.slot.id = :slotId and b.status = 'ACTIVE' " +
           "and b.startTime < :end and b.endTime > :start")
    boolean isOverlapping(@Param("slotId") Long slotId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
