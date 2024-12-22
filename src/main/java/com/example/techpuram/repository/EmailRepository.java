package com.example.techpuram.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.techpuram.Entity.Email;


public interface EmailRepository  extends JpaRepository<Email, Long> {

}
