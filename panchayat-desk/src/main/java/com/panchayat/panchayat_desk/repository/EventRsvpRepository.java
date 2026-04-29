package com.panchayat.panchayat_desk.repository;

import com.panchayat.panchayat_desk.model.EventRsvp;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EventRsvpRepository extends JpaRepository<EventRsvp, Long> {
    Optional<EventRsvp> findByEventIdAndUserId(Long eventId, Long userId);
    List<EventRsvp> findByEventId(Long eventId);
    List<EventRsvp> findByUserId(Long userId);
    long countByEventIdAndStatus(Long eventId, EventRsvp.RsvpStatus status);
}
