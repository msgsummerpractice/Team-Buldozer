package com.example.CheckInApp.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.CheckInApp.model.User;

@Repository
public interface EventRepository extends JpaRepository<User, Long> {

}