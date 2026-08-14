package com.example.CheckInApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "users")
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32)
    private String firstName;

    @Column(length = 32)
    private String lastName;

    @Column(unique = true, length = 64)
    private String email;

    @Column(length = 128)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserLocation location;

    private boolean status;

    @Basic(fetch = FetchType.LAZY)
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name")
    private Set<UserRole> userRoles = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private List<Event> events;

}