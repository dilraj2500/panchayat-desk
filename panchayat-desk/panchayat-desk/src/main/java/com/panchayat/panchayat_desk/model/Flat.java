package com.panchayat.panchayat_desk.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flats")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flat_number", nullable = false)
    private String flatNumber;

    @Column(name = "floor_number")
    private Integer floorNumber;

    @Column(name = "flat_type")
    private String flatType; // 1BHK, 2BHK, 3BHK

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OccupancyType occupancyType = OccupancyType.VACANT;

    @ManyToOne
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @OneToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToOne
    @JoinColumn(name = "tenant_id")
    private User tenant;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public enum OccupancyType {
        OWNER_OCCUPIED, TENANT_OCCUPIED, VACANT
    }
}
