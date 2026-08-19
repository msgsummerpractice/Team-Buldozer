package com.example.CheckInApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.CheckInApp.model.User;
import com.example.CheckInApp.model.UserLocation;
import com.example.CheckInApp.model.UserRole;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByRolesContaining(UserRole role);
    List<User> findByLocation(UserLocation location);
}
