package com.panchayat.panchayat_desk.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private NoticeType type = NoticeType.GENERAL;

    @ManyToOne
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @ManyToOne
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public enum NoticeType {
        GENERAL, WATER, ELECTRICITY, MEETING, MAINTENANCE, SECURITY, OTHER
    }
}
