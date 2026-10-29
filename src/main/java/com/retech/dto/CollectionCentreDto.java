package com.retech.dto;

import com.retech.entity.CollectionCentre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CollectionCentreDto {
    private Long id;

    @NotBlank(message = "Centre name is required")
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

    @NotBlank(message = "Supported categories are required")
    private String supportedCategories;

    private String operatingHours;
    private boolean active = true;

    public CollectionCentreDto() {
    }

    public static CollectionCentreDto fromEntity(CollectionCentre centre) {
        CollectionCentreDto dto = new CollectionCentreDto();
        dto.setId(centre.getId());
        dto.setName(centre.getName());
        dto.setAddress(centre.getAddress());
        dto.setCity(centre.getCity());
        dto.setPostalCode(centre.getPostalCode());
        dto.setContactPhone(centre.getContactPhone());
        dto.setContactEmail(centre.getContactEmail());
        dto.setSupportedCategories(centre.getSupportedCategories());
        dto.setOperatingHours(centre.getOperatingHours());
        dto.setActive(centre.isActive());
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

    public String getSupportedCategories() {
        return supportedCategories;
    }

    public void setSupportedCategories(String supportedCategories) {
        this.supportedCategories = supportedCategories;
    }

    public String getOperatingHours() {
        return operatingHours;
    }

    public void setOperatingHours(String operatingHours) {
        this.operatingHours = operatingHours;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
