package com.panchayat.panchayat_desk.controller;

import com.panchayat.panchayat_desk.model.Event;
import com.panchayat.panchayat_desk.model.EventRsvp;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.service.EventService;
import com.panchayat.panchayat_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    // ---- SECRETARY ----

    @GetMapping("/secretary/events")
    public String secretaryEvents(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("events", eventService.findAllBySociety(user.getSociety().getId()));
        model.addAttribute("newEvent", new Event());
        return "secretary/events";
    }

    @PostMapping("/secretary/event/add")
    public String addEvent(@ModelAttribute Event event,
                           @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        event.setCreatedBy(user);
        event.setSociety(user.getSociety());
        eventService.save(event);
        return "redirect:/secretary/events?added=true";
    }

    @PostMapping("/secretary/event/{id}/cancel")
    public String cancelEvent(@PathVariable Long id) {
        eventService.deactivate(id);
        return "redirect:/secretary/events?cancelled=true";
    }

    // ---- RESIDENT ----

    @GetMapping("/resident/events")
    public String residentEvents(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<Event> events = eventService.findActiveBySociety(user.getSociety().getId());

        // Build rsvp status map for this user
        Map<Long, String> rsvpMap = new HashMap<>();
        for (Event event : events) {
            Optional<EventRsvp> rsvp = eventService.getUserRsvp(event.getId(), user.getId());
            rsvpMap.put(event.getId(), rsvp.map(r -> r.getStatus().name()).orElse("NONE"));
        }

        model.addAttribute("user", user);
        model.addAttribute("events", events);
        model.addAttribute("rsvpMap", rsvpMap);
        model.addAttribute("rsvpStatuses", EventRsvp.RsvpStatus.values());
        return "resident/events";
    }

    @PostMapping("/resident/event/{id}/rsvp")
    public String rsvp(@PathVariable Long id,
                       @RequestParam EventRsvp.RsvpStatus status,
                       @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        eventService.rsvp(id, user, status);
        return "redirect:/resident/events?rsvpd=true";
    }
}
