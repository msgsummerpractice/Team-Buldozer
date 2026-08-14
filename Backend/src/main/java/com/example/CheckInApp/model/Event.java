package com.example.CheckInApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventLocation eventLocation;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus eventStatus;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "event_poster")
    private byte[] poster;

    @Column(nullable = false)
    private Date registrationStartDate;

    @Column(nullable = false)
    private Date registrationEndDate;

    @Column(nullable = false, length = 64)
    private String address;

    @Column(nullable = false, length = 1024)
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private User createdBy;

    @Column(nullable = false)
    private boolean foodProvided;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    
}
