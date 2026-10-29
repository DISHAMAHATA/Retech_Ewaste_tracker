package com.retech.dto;

import com.retech.entity.enums.EwasteCategory;
import com.retech.entity.enums.ItemCondition;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateCollectionRequestDto {

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 150, message = "Item name must be between 2 and 150 characters")
    private String itemName;

    @NotNull(message = "E-waste category is required")
    private EwasteCategory category;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot exceed 100 items per request")
    private Integer quantity;

    @DecimalMin(value = "0.01", message = "Estimated weight must be greater than 0 kg")
    @DecimalMax(value = "500.00", message = "Estimated weight cannot exceed 500 kg")
    private BigDecimal estimatedWeightKg;

    @NotNull(message = "Item condition is required")
    private ItemCondition itemCondition;

    @NotBlank(message = "Pickup address is required")
    @Size(min = 5, max = 255, message = "Pickup address must be between 5 and 255 characters")
    private String pickupAddress;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    @NotBlank(message = "Postal code is required")
    @Size(min = 3, max = 20, message = "Postal code must be between 3 and 20 characters")
    private String postalCode;

    @FutureOrPresent(message = "Preferred pickup date must be today or in the future")
    private LocalDate preferredPickupDate;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @AssertTrue(message = "You must confirm that the provided information is accurate")
    private Boolean accurateInfoConfirmed;

    public CreateCollectionRequestDto() {
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public EwasteCategory getCategory() {
        return category;
    }

    public void setCategory(EwasteCategory category) {
        this.category = category;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getEstimatedWeightKg() {
        return estimatedWeightKg;
    }

    public void setEstimatedWeightKg(BigDecimal estimatedWeightKg) {
        this.estimatedWeightKg = estimatedWeightKg;
    }

    public ItemCondition getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
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

    public LocalDate getPreferredPickupDate() {
        return preferredPickupDate;
    }

    public void setPreferredPickupDate(LocalDate preferredPickupDate) {
        this.preferredPickupDate = preferredPickupDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getAccurateInfoConfirmed() {
        return accurateInfoConfirmed;
    }

    public void setAccurateInfoConfirmed(Boolean accurateInfoConfirmed) {
        this.accurateInfoConfirmed = accurateInfoConfirmed;
    }
}
