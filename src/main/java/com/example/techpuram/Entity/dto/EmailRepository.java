package com.example.techpuram.Entity.dto;

import org.springframework.data.jpa.repository.JpaRepository; // Importing JpaRepository for basic CRUD operations and additional JPA functionalities
import org.springframework.data.jpa.repository.Query; // Importing @Query annotation for custom JPQL queries

import java.util.Optional; // Importing Optional for handling potentially null return values

public interface EmailRepository extends JpaRepository<Email, Long> {
    // Declaring the repository interface for the `Email` entity
    // Extends JpaRepository to provide built-in CRUD operations and pagination capabilities for the Email entity
    // The first generic type `Email` specifies the entity type
    // The second generic type `Long` specifies the type of the primary key (ID)

    // Check if an email exists by its UID
    boolean existsByUid(String uid);
    // Method to check whether an email with the given UID exists in the database
    // Spring Data JPA will automatically generate the implementation based on the method name
    // The `uid` parameter is the unique identifier for the email

    // Custom query to find the max UID in the Email table
    @Query("SELECT MAX(CAST(e.uid AS long)) FROM Email e")
    Optional<Long> findMaxUid();
    // A custom query using JPQL (Java Persistence Query Language) to find the maximum UID stored in the Email table
    // `MAX` is an aggregate function to get the highest value in the `uid` column
    // `CAST(e.uid AS long)` ensures that the UID, stored as a string, is cast to a long for comparison
    // Wrapping the result in `Optional<Long>` helps to handle cases where no emails exist in the table (null-safe handling)
}
