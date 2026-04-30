package com.panchayat.panchayat_desk.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priority priority = Priority.MEDIUM;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "secretary_reply", length = 1000)
    private String secretaryReply;

    @Column(name = "ai_reply", length = 1000)
    private String aiReply;

    @ManyToOne
    @JoinColumn(name = "resident_id", nullable = false)
    private User resident;

    @ManyToOne
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Category {
        WATER, ELECTRICITY, SECURITY,
        CLEANLINESS, MAINTENANCE, NOISE,
        PARKING, OTHER
    }

    public enum Status {
        PENDING, IN_PROGRESS, RESOLVED, REJECTED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
}