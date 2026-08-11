package com.example.CheckInApp.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "users")
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
    private Location location;
    private boolean status;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id")) 
    @Enumerated(EnumType.STRING) 
    @Column(name = "role_name")
    private Set<Role> roles = new HashSet<>(); 
}

 