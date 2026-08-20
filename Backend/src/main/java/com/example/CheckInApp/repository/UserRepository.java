package com.example.CheckInApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("SELECT u.email FROM User u WHERE u.status = true")
    List<String> findActiveEmails();

    @Query("SELECT u.email FROM User u WHERE u.location = :location AND u.status = true")
    List<String> findActiveEmailsByLocation(@Param("location") UserLocation location);
}
