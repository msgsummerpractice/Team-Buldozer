package com.example.CheckInApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "registrations",
        uniqueConstraints = @UniqueConstraint(name = "uq_registrations_event_user", columnNames = {"event_id", "user_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(nullable = false)
    private LocalDate registrationDate;

    private Boolean gdprConsent;

    private Boolean photoConsent;

    @Enumerated(EnumType.STRING)
    private FoodPreference foodPreference;

    private Boolean transportNeeded;

    @Column(length = 64)
    private String driverName;

    @Column(length = 12)
    @NotBlank(message = "Driver Phone Number is required.")
    @Pattern(
            regexp = "^(\\+40|0)[7][0-9]{8}$",
            message = "Phone Number is not a valid romanian phone number (ex: 0722123456 or +40722123456)"
    )
    private String driverPhoneNumber;

    private Boolean accommodationNeeded;

    private Integer accommodationDays;

}
