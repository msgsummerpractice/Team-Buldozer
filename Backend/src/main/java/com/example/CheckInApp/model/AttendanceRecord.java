package com.example.CheckInApp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attendance_records")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AttendanceRecord {

    @Id
    private Float id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean checkedIn;

}
