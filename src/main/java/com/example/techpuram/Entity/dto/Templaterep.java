package com.example.techpuram.Entity.dto;

import org.springframework.data.jpa.repository.JpaRepository;


public interface Templaterep extends JpaRepository<Template, Long>{
    Template findByTemplateName(String templateName);

}
