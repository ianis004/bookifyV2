package com.bookify.service;

import com.bookify.dto.BusinessSettingsDTO;
import com.bookify.entity.BusinessSettings;
import com.bookify.exception.ResourceNotFoundException;
import com.bookify.repository.BusinessSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for BusinessSettings
 */
@Service
@Transactional
public class BusinessSettingsService {

    @Autowired
    private BusinessSettingsRepository businessSettingsRepository;

    public BusinessSettingsDTO getSettings() {
        BusinessSettings settings = businessSettingsRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Business settings not found"));
        return convertToDTO(settings);
    }

    public BusinessSettingsDTO getSettingsById(Long id) {
        BusinessSettings settings = businessSettingsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business settings", id));
        return convertToDTO(settings);
    }

    public BusinessSettingsDTO updateSettings(Long id, BusinessSettingsDTO dto) {
        BusinessSettings settings = businessSettingsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business settings", id));

        settings.setBusinessName(dto.getBusinessName());
        settings.setOpeningTime(dto.getOpeningTime());
        settings.setClosingTime(dto.getClosingTime());
        settings.setSlotDurationMinutes(dto.getSlotDurationMinutes());
        settings.setAcceptOnlineBookings(dto.getAcceptOnlineBookings());
        settings.setContactEmail(dto.getContactEmail());
        settings.setContactPhone(dto.getContactPhone());
        settings.setAddress(dto.getAddress());

        BusinessSettings updated = businessSettingsRepository.save(settings);
        return convertToDTO(updated);
    }

    public BusinessSettingsDTO createSettings(BusinessSettingsDTO dto) {
        BusinessSettings settings = convertToEntity(dto);
        BusinessSettings saved = businessSettingsRepository.save(settings);
        return convertToDTO(saved);
    }

    public boolean settingsExist() {
        return businessSettingsRepository.count() > 0;
    }

    private BusinessSettingsDTO convertToDTO(BusinessSettings settings) {
        return BusinessSettingsDTO.builder()
                .id(settings.getId())
                .businessName(settings.getBusinessName())
                .openingTime(settings.getOpeningTime())
                .closingTime(settings.getClosingTime())
                .slotDurationMinutes(settings.getSlotDurationMinutes())
                .acceptOnlineBookings(settings.getAcceptOnlineBookings())
                .contactEmail(settings.getContactEmail())
                .contactPhone(settings.getContactPhone())
                .address(settings.getAddress())
                .build();
    }

    private BusinessSettings convertToEntity(BusinessSettingsDTO dto) {
        return BusinessSettings.builder()
                .businessName(dto.getBusinessName())
                .openingTime(dto.getOpeningTime())
                .closingTime(dto.getClosingTime())
                .slotDurationMinutes(dto.getSlotDurationMinutes())
                .acceptOnlineBookings(dto.getAcceptOnlineBookings())
                .contactEmail(dto.getContactEmail())
                .contactPhone(dto.getContactPhone())
                .address(dto.getAddress())
                .build();
    }
}