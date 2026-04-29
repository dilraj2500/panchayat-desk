package com.panchayat.panchayat_desk.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "resident_id", nullable = false)
    private User resident;

    @ManyToOne
    @JoinColumn(name = "society_id", nullable = false)
    private Society society;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "bill_month", nullable = false)
    private String billMonth; // e.g. "April 2025"

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BillStatus status = BillStatus.UNPAID;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public enum BillStatus {
        UNPAID, PAID, OVERDUE
    }
}
