package com.panchayat.panchayat_desk.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "societies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Society {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    private String city;

    private String state;

    @Column(name = "pin_code")
    private String pinCode;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "secretary_email")
    private String secretaryEmail;

    @Column(name = "max_residents")
    @Builder.Default
    private Integer maxResidents = 2000;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "society", cascade = CascadeType.ALL)
    private List<User> users;

    @OneToMany(mappedBy = "society", cascade = CascadeType.ALL)
    private List<Complaint> complaints;

    @OneToMany(mappedBy = "society", cascade = CascadeType.ALL)
    private List<Flat> flats;

    @OneToMany(mappedBy = "society", cascade = CascadeType.ALL)
    private List<Notice> notices;

    @OneToMany(mappedBy = "society", cascade = CascadeType.ALL)
    private List<Event> events;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}