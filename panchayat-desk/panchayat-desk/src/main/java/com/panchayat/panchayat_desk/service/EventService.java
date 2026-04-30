package com.panchayat.panchayat_desk.service;

import com.panchayat.panchayat_desk.model.Event;
import com.panchayat.panchayat_desk.model.EventRsvp;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.repository.EventRepository;
import com.panchayat.panchayat_desk.repository.EventRsvpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventRsvpRepository rsvpRepository;

    public Event save(Event event) {
        return eventRepository.save(event);
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found!"));
    }

    public List<Event> findActiveBySociety(Long societyId) {
        return eventRepository.findBySocietyIdAndIsActiveTrueOrderByEventDateDesc(societyId);
    }

    public List<Event> findAllBySociety(Long societyId) {
        return eventRepository.findBySocietyIdOrderByEventDateDesc(societyId);
    }

    public EventRsvp rsvp(Long eventId, User user, EventRsvp.RsvpStatus status) {
        Optional<EventRsvp> existing = rsvpRepository.findByEventIdAndUserId(eventId, user.getId());
        if (existing.isPresent()) {
            EventRsvp rsvp = existing.get();
            rsvp.setStatus(status);
            return rsvpRepository.save(rsvp);
        }
        Event event = findById(eventId);
        EventRsvp rsvp = EventRsvp.builder()
                .event(event)
                .user(user)
                .status(status)
                .build();
        return rsvpRepository.save(rsvp);
    }

    public Optional<EventRsvp> getUserRsvp(Long eventId, Long userId) {
        return rsvpRepository.findByEventIdAndUserId(eventId, userId);
    }

    public long countGoing(Long eventId) {
        return rsvpRepository.countByEventIdAndStatus(eventId, EventRsvp.RsvpStatus.GOING);
    }

    public void deactivate(Long id) {
        Event event = findById(id);
        event.setIsActive(false);
        eventRepository.save(event);
    }
}
