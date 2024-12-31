package com.example.techpuram.Entity.dto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Long> {

    // Check if an email exists by its UID
    boolean existsByUid(String uid);

    // Custom query to find the max UID in the Email table
    @Query("SELECT MAX(CAST(e.uid AS long)) FROM Email e")
    Optional<Long> findMaxUid();
}


