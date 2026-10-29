package com.retech.entity;

import com.retech.entity.enums.EwasteCategory;
import com.retech.entity.enums.ItemCondition;
import com.retech.entity.enums.RequestStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "collection_requests")
public class CollectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_code", nullable = false, unique = true, length = 30)
    private String trackingCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Column(name = "item_name", nullable = false, length = 150)
    private String itemName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private EwasteCategory category;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "estimated_weight_kg", precision = 8, scale = 2)
    private BigDecimal estimatedWeightKg;

    @Column(name = "actual_weight_kg", precision = 8, scale = 2)
    private BigDecimal actualWeightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_condition", nullable = false, length = 50)
    private ItemCondition itemCondition;

    @Column(name = "pickup_address", nullable = false, length = 255)
    private String pickupAddress;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "preferred_pickup_date")
    private LocalDate preferredPickupDate;

    @Column(name = "actual_pickup_date")
    private LocalDate actualPickupDate;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private RequestStatus status = RequestStatus.REQUEST_SUBMITTED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private User assignedAgent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_facility_id")
    private RecyclingFacility assignedFacility;

    @OneToMany(mappedBy = "collectionRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<RequestStatusHistory> statusHistory = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public CollectionRequest() {
    }

    public CollectionRequest(String trackingCode, User customer, String itemName, EwasteCategory category,
                             Integer quantity, BigDecimal estimatedWeightKg, ItemCondition itemCondition,
                             String pickupAddress, String city, String postalCode,
                             LocalDate preferredPickupDate, String notes) {
        this.trackingCode = trackingCode;
        this.customer = customer;
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.estimatedWeightKg = estimatedWeightKg;
        this.itemCondition = itemCondition;
        this.pickupAddress = pickupAddress;
        this.city = city;
        this.postalCode = postalCode;
        this.preferredPickupDate = preferredPickupDate;
        this.notes = notes;
        this.status = RequestStatus.REQUEST_SUBMITTED;
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

    public void addStatusHistory(RequestStatus previousStatus, RequestStatus newStatus, User actor, String actorName, String remarks) {
        RequestStatusHistory history = new RequestStatusHistory(this, previousStatus, newStatus, actor, actorName, remarks);
        this.statusHistory.add(history);
        this.status = newStatus;
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

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
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

    public User getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(User assignedAgent) {
        this.assignedAgent = assignedAgent;
    }

    public RecyclingFacility getAssignedFacility() {
        return assignedFacility;
    }

    public void setAssignedFacility(RecyclingFacility assignedFacility) {
        this.assignedFacility = assignedFacility;
    }

    public List<RequestStatusHistory> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<RequestStatusHistory> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
