package com.retech.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recycling_facilities")
public class RecyclingFacility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 100)
    private String contactEmail;

    @Column(name = "capacity_kg_per_day", precision = 10, scale = 2)
    private BigDecimal capacityKgPerDay = new BigDecimal("1000.00");

    @Column(name = "supported_materials", length = 500)
    private String supportedMaterials;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public RecyclingFacility() {
    }

    public RecyclingFacility(String name, String address, String city, String postalCode,
                             String contactPhone, String contactEmail, BigDecimal capacityKgPerDay,
                             String supportedMaterials) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.capacityKgPerDay = capacityKgPerDay;
        this.supportedMaterials = supportedMaterials;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
