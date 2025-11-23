package com.bookify.controller;

import com.bookify.dto.BusinessSettingsDTO;
import com.bookify.service.BusinessSettingsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *REST API for business settings
 */
@RestController
@RequestMapping("/api/settings")
public class BusinessSettingsController {

    @Autowired
    private BusinessSettingsService businessSettingsService;

    @GetMapping
    public ResponseEntity<BusinessSettingsDTO> getSettings() {
        return ResponseEntity.ok(businessSettingsService.getSettings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessSettingsDTO> getSettingsById(@PathVariable Long id) {
        return ResponseEntity.ok(businessSettingsService.getSettingsById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessSettingsDTO> updateSettings(
            @PathVariable Long id,
            @Valid @RequestBody BusinessSettingsDTO dto) {
        return ResponseEntity.ok(businessSettingsService.updateSettings(id, dto));
    }

    @PostMapping
    public ResponseEntity<BusinessSettingsDTO> createSettings(
            @Valid @RequestBody BusinessSettingsDTO dto) {
        return ResponseEntity.ok(businessSettingsService.createSettings(dto));
    }
}