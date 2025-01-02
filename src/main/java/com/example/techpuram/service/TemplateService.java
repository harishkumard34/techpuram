package com.example.techpuram.service;

import com.example.techpuram.Entity.dto.Template;
import com.example.techpuram.Entity.dto.Templaterep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {

    @Autowired
    private Templaterep templateRepository;

    // Get all templates
    public List<Template> getAllTemplates() {
        return templateRepository.findAll();
    }

    // Get template by ID
    public Optional<Template> getTemplateById(Long id) {
        return templateRepository.findById(id);
    }

    // Create a new template
    public Template createTemplate(Template template) {
        return templateRepository.save(template);
    }

    // Update an existing template
    public Optional<Template> updateTemplate(Long id, Template template) {
        if (templateRepository.existsById(id)) {
            template.setTemplateId(id);
            return Optional.of(templateRepository.save(template));
        }
        return Optional.empty();
    }

    // Delete a template
    public boolean deleteTemplate(Long id) {
        if (templateRepository.existsById(id)) {
            templateRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
