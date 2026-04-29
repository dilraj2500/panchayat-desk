package com.panchayat.panchayat_desk.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_rsvps",
       uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRsvp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RsvpStatus status = RsvpStatus.GOING;

    @Column(name = "rsvp_at")
    private LocalDateTime rsvpAt;

    @PrePersist
    public void prePersist() {
        this.rsvpAt = LocalDateTime.now();
    }

    public enum RsvpStatus {
        GOING, NOT_GOING, MAYBE
    }
}
