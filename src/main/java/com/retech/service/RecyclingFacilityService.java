package com.retech.service;

import com.retech.dto.RecyclingFacilityDto;
import com.retech.entity.RecyclingFacility;
import com.retech.exception.ResourceNotFoundException;
import com.retech.repository.RecyclingFacilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecyclingFacilityService {

    private final RecyclingFacilityRepository facilityRepository;

    public RecyclingFacilityService(RecyclingFacilityRepository facilityRepository) {
        this.facilityRepository = facilityRepository;
    }

    public List<RecyclingFacilityDto> getAllActiveFacilities() {
        return facilityRepository.findByActiveTrue().stream()
                .map(RecyclingFacilityDto::fromEntity)
                .collect(Collectors.toList());
    }

    public RecyclingFacilityDto getFacilityById(Long id) {
        RecyclingFacility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recycling facility not found with ID: " + id));
        return RecyclingFacilityDto.fromEntity(facility);
    }

    @Transactional
    public RecyclingFacilityDto createFacility(RecyclingFacilityDto dto) {
        RecyclingFacility facility = new RecyclingFacility(
                dto.getName().trim(),
                dto.getAddress().trim(),
                dto.getCity().trim(),
                dto.getPostalCode().trim(),
                dto.getContactPhone() != null ? dto.getContactPhone().trim() : null,
                dto.getContactEmail() != null ? dto.getContactEmail().trim() : null,
                dto.getCapacityKgPerDay(),
                dto.getSupportedMaterials() != null ? dto.getSupportedMaterials().trim() : null
        );
        RecyclingFacility saved = facilityRepository.save(facility);
        return RecyclingFacilityDto.fromEntity(saved);
    }

    @Transactional
    public RecyclingFacilityDto updateFacility(Long id, RecyclingFacilityDto dto) {
        RecyclingFacility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recycling facility not found with ID: " + id));

        facility.setName(dto.getName().trim());
        facility.setAddress(dto.getAddress().trim());
        facility.setCity(dto.getCity().trim());
        facility.setPostalCode(dto.getPostalCode().trim());
        facility.setContactPhone(dto.getContactPhone() != null ? dto.getContactPhone().trim() : null);
        facility.setContactEmail(dto.getContactEmail() != null ? dto.getContactEmail().trim() : null);
        facility.setCapacityKgPerDay(dto.getCapacityKgPerDay());
        facility.setSupportedMaterials(dto.getSupportedMaterials() != null ? dto.getSupportedMaterials().trim() : null);
        facility.setActive(dto.isActive());

        RecyclingFacility saved = facilityRepository.save(facility);
        return RecyclingFacilityDto.fromEntity(saved);
    }

    @Transactional
    public void deleteFacility(Long id) {
        RecyclingFacility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recycling facility not found with ID: " + id));
        facility.setActive(false); // Soft-delete
        facilityRepository.save(facility);
    }
}
