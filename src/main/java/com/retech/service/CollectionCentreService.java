package com.retech.service;

import com.retech.dto.CollectionCentreDto;
import com.retech.entity.CollectionCentre;
import com.retech.exception.ResourceNotFoundException;
import com.retech.repository.CollectionCentreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CollectionCentreService {

    private final CollectionCentreRepository centreRepository;

    public CollectionCentreService(CollectionCentreRepository centreRepository) {
        this.centreRepository = centreRepository;
    }

    public List<CollectionCentreDto> getAllActiveCentres() {
        return centreRepository.findByActiveTrue().stream()
                .map(CollectionCentreDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<CollectionCentreDto> searchCentres(String city, String category) {
        String cityParam = (city != null && !city.isBlank()) ? city.trim() : null;
        String categoryParam = (category != null && !category.isBlank()) ? category.trim() : null;
        return centreRepository.searchCentres(cityParam, categoryParam).stream()
                .map(CollectionCentreDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<String> getAvailableCities() {
        return centreRepository.findDistinctCities();
    }

    public CollectionCentreDto getCentreById(Long id) {
        CollectionCentre centre = centreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection centre not found with ID: " + id));
        return CollectionCentreDto.fromEntity(centre);
    }

    @Transactional
    public CollectionCentreDto createCentre(CollectionCentreDto dto) {
        CollectionCentre centre = new CollectionCentre(
                dto.getName().trim(),
                dto.getAddress().trim(),
                dto.getCity().trim(),
                dto.getPostalCode().trim(),
                dto.getContactPhone() != null ? dto.getContactPhone().trim() : null,
                dto.getContactEmail() != null ? dto.getContactEmail().trim() : null,
                dto.getSupportedCategories().trim(),
                dto.getOperatingHours() != null ? dto.getOperatingHours().trim() : null
        );
        CollectionCentre saved = centreRepository.save(centre);
        return CollectionCentreDto.fromEntity(saved);
    }

    @Transactional
    public CollectionCentreDto updateCentre(Long id, CollectionCentreDto dto) {
        CollectionCentre centre = centreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection centre not found with ID: " + id));

        centre.setName(dto.getName().trim());
        centre.setAddress(dto.getAddress().trim());
        centre.setCity(dto.getCity().trim());
        centre.setPostalCode(dto.getPostalCode().trim());
        centre.setContactPhone(dto.getContactPhone() != null ? dto.getContactPhone().trim() : null);
        centre.setContactEmail(dto.getContactEmail() != null ? dto.getContactEmail().trim() : null);
        centre.setSupportedCategories(dto.getSupportedCategories().trim());
        centre.setOperatingHours(dto.getOperatingHours() != null ? dto.getOperatingHours().trim() : null);
        centre.setActive(dto.isActive());

        CollectionCentre saved = centreRepository.save(centre);
        return CollectionCentreDto.fromEntity(saved);
    }

    @Transactional
    public void deleteCentre(Long id) {
        CollectionCentre centre = centreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection centre not found with ID: " + id));
        centre.setActive(false); // Soft-delete
        centreRepository.save(centre);
    }
}
