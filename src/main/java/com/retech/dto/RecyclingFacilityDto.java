package com.retech.dto;

import com.retech.entity.RecyclingFacility;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class RecyclingFacilityDto {
    private Long id;

    @NotBlank(message = "Facility name is required")
    @Size(min = 2, max = 150)
    private String name;

    @NotBlank(message = "Address is required")
    @Size(max = 255)
    private String address;

    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "Postal code is required")
    @Size(max = 20)
    private String postalCode;

    private String contactPhone;
    private String contactEmail;

    @DecimalMin(value = "10.00", message = "Capacity must be at least 10 kg/day")
    private BigDecimal capacityKgPerDay;

    private String supportedMaterials;
    private boolean active = true;

    public RecyclingFacilityDto() {
    }

    public static RecyclingFacilityDto fromEntity(RecyclingFacility facility) {
        RecyclingFacilityDto dto = new RecyclingFacilityDto();
        dto.setId(facility.getId());
        dto.setName(facility.getName());
        dto.setAddress(facility.getAddress());
        dto.setCity(facility.getCity());
        dto.setPostalCode(facility.getPostalCode());
        dto.setContactPhone(facility.getContactPhone());
        dto.setContactEmail(facility.getContactEmail());
        dto.setCapacityKgPerDay(facility.getCapacityKgPerDay());
        dto.setSupportedMaterials(facility.getSupportedMaterials());
        dto.setActive(facility.isActive());
        return dto;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public BigDecimal getCapacityKgPerDay() {
        return capacityKgPerDay;
    }

    public void setCapacityKgPerDay(BigDecimal capacityKgPerDay) {
        this.capacityKgPerDay = capacityKgPerDay;
    }

    public String getSupportedMaterials() {
        return supportedMaterials;
    }

    public void setSupportedMaterials(String supportedMaterials) {
        this.supportedMaterials = supportedMaterials;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
