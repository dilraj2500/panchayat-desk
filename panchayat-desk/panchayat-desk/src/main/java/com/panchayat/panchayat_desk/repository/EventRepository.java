package com.panchayat.panchayat_desk.repository;

import com.panchayat.panchayat_desk.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findBySocietyIdAndIsActiveTrueOrderByEventDateDesc(Long societyId);
    List<Event> findBySocietyIdOrderByEventDateDesc(Long societyId);
}
