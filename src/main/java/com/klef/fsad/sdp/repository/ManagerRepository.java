package com.klef.fsad.sdp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.klef.fsad.sdp.model.Manager;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Manager findByUsernameAndPassword(String username, String password);
    Manager findByEmail(String email);
    Manager findByUsername(String username); // Added this based on Service usage
}