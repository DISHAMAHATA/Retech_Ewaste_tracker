package com.retech.dto;

import com.retech.entity.CollectionRequest;
import com.retech.entity.enums.EwasteCategory;
import com.retech.entity.enums.ItemCondition;
import com.retech.entity.enums.RequestStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionRequestResponseDto {
    private Long id;
    private String trackingCode;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String itemName;
    private EwasteCategory category;
    private String categoryLabel;
    private Integer quantity;
    private BigDecimal estimatedWeightKg;
    private BigDecimal actualWeightKg;
    private ItemCondition itemCondition;
    private String itemConditionLabel;
    private String pickupAddress;
    private String city;
    private String postalCode;
    private LocalDate preferredPickupDate;
    private LocalDate actualPickupDate;
    private String notes;
    private RequestStatus status;
    private String statusDisplayName;
    private int stageOrder;
    private boolean cancellable;
    private Long assignedAgentId;
    private String assignedAgentName;
    private String assignedAgentPhone;
    private Long assignedFacilityId;
    private String assignedFacilityName;
    private List<RequestStatusHistoryDto> statusHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CollectionRequestResponseDto() {
    }

    public static CollectionRequestResponseDto fromEntity(CollectionRequest req) {
        CollectionRequestResponseDto dto = new CollectionRequestResponseDto();
        dto.setId(req.getId());
        dto.setTrackingCode(req.getTrackingCode());
        if (req.getCustomer() != null) {
            dto.setCustomerId(req.getCustomer().getId());
            dto.setCustomerName(req.getCustomer().getFullName());
            dto.setCustomerEmail(req.getCustomer().getEmail());
            dto.setCustomerPhone(req.getCustomer().getPhone());
        }
        dto.setItemName(req.getItemName());
        dto.setCategory(req.getCategory());
        dto.setCategoryLabel(req.getCategory().getLabel());
        dto.setQuantity(req.getQuantity());
        dto.setEstimatedWeightKg(req.getEstimatedWeightKg());
        dto.setActualWeightKg(req.getActualWeightKg());
        dto.setItemCondition(req.getItemCondition());
        dto.setItemConditionLabel(req.getItemCondition().getLabel());
        dto.setPickupAddress(req.getPickupAddress());
        dto.setCity(req.getCity());
        dto.setPostalCode(req.getPostalCode());
        dto.setPreferredPickupDate(req.getPreferredPickupDate());
        dto.setActualPickupDate(req.getActualPickupDate());
        dto.setNotes(req.getNotes());
        dto.setStatus(req.getStatus());
        dto.setStatusDisplayName(req.getStatus().getDisplayName());
        dto.setStageOrder(req.getStatus().getStageOrder());
        dto.setCancellable(req.getStatus().isCancellableByCustomer());

        if (req.getAssignedAgent() != null) {
            dto.setAssignedAgentId(req.getAssignedAgent().getId());
            dto.setAssignedAgentName(req.getAssignedAgent().getFullName());
            dto.setAssignedAgentPhone(req.getAssignedAgent().getPhone());
        }
        if (req.getAssignedFacility() != null) {
            dto.setAssignedFacilityId(req.getAssignedFacility().getId());
            dto.setAssignedFacilityName(req.getAssignedFacility().getName());
        }

        if (req.getStatusHistory() != null) {
            dto.setStatusHistory(req.getStatusHistory().stream()
                    .map(RequestStatusHistoryDto::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            dto.setStatusHistory(Collections.emptyList());
        }

        dto.setCreatedAt(req.getCreatedAt());
        dto.setUpdatedAt(req.getUpdatedAt());
        return dto;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrackingCode() {
        return trackingCode;
    }

    public void setTrackingCode(String trackingCode) {
        this.trackingCode = trackingCode;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
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

    public String getCategoryLabel() {
        return categoryLabel;
    }

    public void setCategoryLabel(String categoryLabel) {
        this.categoryLabel = categoryLabel;
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

    public BigDecimal getActualWeightKg() {
        return actualWeightKg;
    }

    public void setActualWeightKg(BigDecimal actualWeightKg) {
        this.actualWeightKg = actualWeightKg;
    }

    public ItemCondition getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public String getItemConditionLabel() {
        return itemConditionLabel;
    }

    public void setItemConditionLabel(String itemConditionLabel) {
        this.itemConditionLabel = itemConditionLabel;
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

    public LocalDate getActualPickupDate() {
        return actualPickupDate;
    }

    public void setActualPickupDate(LocalDate actualPickupDate) {
        this.actualPickupDate = actualPickupDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getStatusDisplayName() {
        return statusDisplayName;
    }

    public void setStatusDisplayName(String statusDisplayName) {
        this.statusDisplayName = statusDisplayName;
    }

    public int getStageOrder() {
        return stageOrder;
    }

    public void setStageOrder(int stageOrder) {
        this.stageOrder = stageOrder;
    }

    public boolean isCancellable() {
        return cancellable;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public Long getAssignedAgentId() {
        return assignedAgentId;
    }

    public void setAssignedAgentId(Long assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    public String getAssignedAgentName() {
        return assignedAgentName;
    }

    public void setAssignedAgentName(String assignedAgentName) {
        this.assignedAgentName = assignedAgentName;
    }

    public String getAssignedAgentPhone() {
        return assignedAgentPhone;
    }

    public void setAssignedAgentPhone(String assignedAgentPhone) {
        this.assignedAgentPhone = assignedAgentPhone;
    }

    public Long getAssignedFacilityId() {
        return assignedFacilityId;
    }

    public void setAssignedFacilityId(Long assignedFacilityId) {
        this.assignedFacilityId = assignedFacilityId;
    }

    public String getAssignedFacilityName() {
        return assignedFacilityName;
    }

    public void setAssignedFacilityName(String assignedFacilityName) {
        this.assignedFacilityName = assignedFacilityName;
    }

    public List<RequestStatusHistoryDto> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<RequestStatusHistoryDto> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
