package com.example.CheckInApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Event {

    private static final String CHECKIN_CODE_REGEX = "^[0-9]{6}$";

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

    @Column(length = 6, unique = true)
    @Pattern(regexp = CHECKIN_CODE_REGEX)
    private String checkInCode;

    /**
     * Base64 encoded string representation of the QR code PNG image for the event.
     */
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "qr_code")
    private byte[] qrCode;

}
