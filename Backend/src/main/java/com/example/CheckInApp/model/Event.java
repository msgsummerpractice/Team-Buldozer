package com.example.CheckInApp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 256)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventLocation location;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "poster")
    private byte[] poster;

    @Column(nullable = false)
    private LocalDate registrationStartDate;

    @Column(nullable = false)
    private LocalDate registrationEndDate;

    @Column(nullable = false, length = 128)
    private String address;

    @Column(nullable = false, length = 1024)
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User createdBy;

    private Boolean foodProvided;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String checkInCode;

    @OneToMany(mappedBy = "event")
    private List<AttendanceRecord> attendanceRecords;

}
