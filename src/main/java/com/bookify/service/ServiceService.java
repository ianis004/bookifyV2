package com.bookify.service;

import com.bookify.dto.ServiceDTO;
import com.bookify.exception.ResourceNotFoundException;
import com.bookify.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ServiceService - Business logic for Service management
 */
@Service
@Transactional
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<ServiceDTO> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceDTO> getActiveServices() {
        return serviceRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ServiceDTO getServiceById(Long id) {
        com.bookify.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        return convertToDTO(service);
    }

    public ServiceDTO createService(ServiceDTO serviceDTO) {
        com.bookify.entity.Service service = convertToEntity(serviceDTO);
        com.bookify.entity.Service saved = serviceRepository.save(service);
        return convertToDTO(saved);
    }

    public ServiceDTO updateService(Long id, ServiceDTO serviceDTO) {
        com.bookify.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));

        service.setName(serviceDTO.getName());
        service.setDescription(serviceDTO.getDescription());
        service.setDurationMinutes(serviceDTO.getDurationMinutes());
        service.setPrice(serviceDTO.getPrice());
        service.setActive(serviceDTO.getActive());

        com.bookify.entity.Service updated = serviceRepository.save(service);
        return convertToDTO(updated);
    }

    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service", id);
        }
        serviceRepository.deleteById(id);
    }

    private ServiceDTO convertToDTO(com.bookify.entity.Service service) {
        return ServiceDTO.builder()
                .id(service.getId())
                .name(service.getName())
                .description(service.getDescription())
                .durationMinutes(service.getDurationMinutes())
                .price(service.getPrice())
                .active(service.getActive())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .build();
    }

    private com.bookify.entity.Service convertToEntity(ServiceDTO dto) {
        return com.bookify.entity.Service.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .durationMinutes(dto.getDurationMinutes())
                .price(dto.getPrice())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }
}