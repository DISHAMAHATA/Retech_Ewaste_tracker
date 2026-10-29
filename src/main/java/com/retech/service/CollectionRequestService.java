package com.retech.service;

import com.retech.dto.*;
import com.retech.entity.CollectionRequest;
import com.retech.entity.RecyclingFacility;
import com.retech.entity.User;
import com.retech.entity.enums.EwasteCategory;
import com.retech.entity.enums.RequestStatus;
import com.retech.entity.enums.Role;
import com.retech.exception.InvalidStatusTransitionException;
import com.retech.exception.ResourceNotFoundException;
import com.retech.exception.UnauthorizedAccessException;
import com.retech.repository.CollectionRequestRepository;
import com.retech.repository.RecyclingFacilityRepository;
import com.retech.repository.UserRepository;
import com.retech.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectionRequestService {

    private static final String CODE_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"; // Omits 0, 1, I, O to avoid visual confusion
    private static final SecureRandom RANDOM = new SecureRandom();

    private final CollectionRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RecyclingFacilityRepository facilityRepository;
    private final SecurityUtils securityUtils;

    public CollectionRequestService(CollectionRequestRepository requestRepository,
                                    UserRepository userRepository,
                                    RecyclingFacilityRepository facilityRepository,
                                    SecurityUtils securityUtils) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.facilityRepository = facilityRepository;
        this.securityUtils = securityUtils;
    }

    /**
     * Generates a unique tracking code such as RET-2026-A1B2C3D4.
     */
    public synchronized String generateUniqueTrackingCode() {
        int currentYear = Year.now().getValue();
        String code;
        int attempts = 0;
        do {
            if (++attempts > 20) {
                throw new IllegalStateException("Failed to generate a unique tracking code after multiple attempts");
            }
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            code = String.format("RET-%d-%s", currentYear, sb.toString());
        } while (requestRepository.existsByTrackingCode(code));

        return code;
    }

    @Transactional
    public CollectionRequestResponseDto createRequest(CreateCollectionRequestDto dto) {
        User customer = securityUtils.getCurrentAuthenticatedUser();
        String trackingCode = generateUniqueTrackingCode();

        CollectionRequest request = new CollectionRequest(
                trackingCode,
                customer,
                dto.getItemName().trim(),
                dto.getCategory(),
                dto.getQuantity(),
                dto.getEstimatedWeightKg(),
                dto.getItemCondition(),
                dto.getPickupAddress().trim(),
                dto.getCity().trim(),
                dto.getPostalCode().trim(),
                dto.getPreferredPickupDate(),
                dto.getNotes() != null ? dto.getNotes().trim() : null
        );

        request.addStatusHistory(
                null,
                RequestStatus.REQUEST_SUBMITTED,
                customer,
                customer.getFullName(),
                "Initial e-waste collection request submitted by customer"
        );

        CollectionRequest saved = requestRepository.save(request);
        return CollectionRequestResponseDto.fromEntity(saved);
    }

    public CollectionRequestResponseDto getByTrackingCode(String trackingCode) {
        CollectionRequest req = requestRepository.findByTrackingCode(trackingCode.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("No e-waste collection request found for tracking code: " + trackingCode));
        return CollectionRequestResponseDto.fromEntity(req);
    }

    public CollectionRequestResponseDto getRequestById(Long id) {
        CollectionRequest req = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection request not found with ID: " + id));

        User currentUser = securityUtils.getCurrentAuthenticatedUser();

        // Customer can only view their own requests
        if (currentUser.getRole() == Role.ROLE_CUSTOMER && !req.getCustomer().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to view this request");
        }

        // Agent can only view requests assigned to them (or public tracking)
        if (currentUser.getRole() == Role.ROLE_AGENT &&
                (req.getAssignedAgent() == null || !req.getAssignedAgent().getId().equals(currentUser.getId()))) {
            throw new UnauthorizedAccessException("You are only authorized to view requests assigned to your route");
        }

        return CollectionRequestResponseDto.fromEntity(req);
    }

    public List<CollectionRequestResponseDto> getCustomerRequests() {
        User customer = securityUtils.getCurrentAuthenticatedUser();
        return requestRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId()).stream()
                .map(CollectionRequestResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CollectionRequestResponseDto cancelRequest(Long id, String reason) {
        CollectionRequest req = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection request not found with ID: " + id));

        User currentUser = securityUtils.getCurrentAuthenticatedUser();

        // Security check: Only the owning customer or admin can cancel
        if (currentUser.getRole() == Role.ROLE_CUSTOMER && !req.getCustomer().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You are not authorized to cancel this request");
        }

        if (!req.getStatus().isCancellableByCustomer() && currentUser.getRole() == Role.ROLE_CUSTOMER) {
            throw new InvalidStatusTransitionException("This request cannot be cancelled at stage: " + req.getStatus().getDisplayName());
        }

        RequestStatus previous = req.getStatus();
        String remarks = (reason != null && !reason.isBlank()) ? reason : "Cancelled by " + currentUser.getFullName();
        req.addStatusHistory(previous, RequestStatus.CANCELLED, currentUser, currentUser.getFullName(), remarks);

        CollectionRequest saved = requestRepository.save(req);
        return CollectionRequestResponseDto.fromEntity(saved);
    }

    // --- AGENT WORKFLOW ---

    public List<CollectionRequestResponseDto> getAgentAssignedRequests() {
        User agent = securityUtils.getCurrentAuthenticatedUser();
        return requestRepository.findByAssignedAgentIdOrderByCreatedAtDesc(agent.getId()).stream()
                .map(CollectionRequestResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CollectionRequestResponseDto agentUpdateStatus(Long id, UpdateStatusDto dto) {
        CollectionRequest req = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection request not found with ID: " + id));

        User agent = securityUtils.getCurrentAuthenticatedUser();
        if (agent.getRole() == Role.ROLE_AGENT &&
                (req.getAssignedAgent() == null || !req.getAssignedAgent().getId().equals(agent.getId()))) {
            throw new UnauthorizedAccessException("You can only update requests assigned directly to you");
        }

        // Permitted agent transitions: e.g. PICKUP_SCHEDULED -> COLLECTED
        if (dto.getNewStatus() == RequestStatus.COLLECTED) {
            if (req.getStatus() != RequestStatus.PICKUP_SCHEDULED && agent.getRole() == Role.ROLE_AGENT) {
                throw new InvalidStatusTransitionException("Request must be in PICKUP_SCHEDULED status before marking as COLLECTED");
            }
            if (dto.getActualWeightKg() != null) {
                req.setActualWeightKg(dto.getActualWeightKg());
            }
            req.setActualPickupDate(dto.getActualPickupDate() != null ? dto.getActualPickupDate() : LocalDate.now());
        } else if (dto.getNewStatus() != RequestStatus.PICKUP_SCHEDULED) {
            throw new InvalidStatusTransitionException("Agents are only permitted to update status to PICKUP_SCHEDULED or COLLECTED");
        }

        RequestStatus previous = req.getStatus();
        req.addStatusHistory(
                previous,
                dto.getNewStatus(),
                agent,
                agent.getFullName(),
                dto.getRemarks() != null ? dto.getRemarks() : "Pickup status updated by agent"
        );

        CollectionRequest saved = requestRepository.save(req);
        return CollectionRequestResponseDto.fromEntity(saved);
    }

    // --- RECYCLING FACILITY WORKFLOW ---

    public List<CollectionRequestResponseDto> getFacilityAssignedRequests(Long facilityId) {
        return requestRepository.findByAssignedFacilityIdOrderByCreatedAtDesc(facilityId).stream()
                .map(CollectionRequestResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<CollectionRequestResponseDto> getRequestsAwaitingFacilityIntake() {
        return requestRepository.findByStatus(RequestStatus.COLLECTED).stream()
                .map(CollectionRequestResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CollectionRequestResponseDto facilityUpdateStatus(Long id, UpdateStatusDto dto) {
        CollectionRequest req = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection request not found with ID: " + id));

        User operator = securityUtils.getCurrentAuthenticatedUser();

        // Permitted facility stages: RECEIVED_AT_FACILITY, UNDER_PROCESSING, RECYCLING_COMPLETED
        if (!req.getStatus().canTransitionTo(dto.getNewStatus()) && operator.getRole() != Role.ROLE_ADMIN) {
            throw new InvalidStatusTransitionException(
                    String.format("Cannot transition from %s to %s", req.getStatus().getDisplayName(), dto.getNewStatus().getDisplayName())
            );
        }

        RequestStatus previous = req.getStatus();
        req.addStatusHistory(
                previous,
                dto.getNewStatus(),
                operator,
                operator.getFullName(),
                dto.getRemarks() != null ? dto.getRemarks() : "Facility status updated"
        );

        CollectionRequest saved = requestRepository.save(req);
        return CollectionRequestResponseDto.fromEntity(saved);
    }

    // --- ADMIN WORKFLOW ---

    public Page<CollectionRequestResponseDto> searchAllRequests(RequestStatus status, EwasteCategory category, String search, Pageable pageable) {
        return requestRepository.searchRequests(status, category, search, pageable)
                .map(CollectionRequestResponseDto::fromEntity);
    }

    @Transactional
    public CollectionRequestResponseDto assignAgent(Long id, AssignAgentDto dto) {
        CollectionRequest req = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection request not found with ID: " + id));

        User agent = userRepository.findById(dto.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with ID: " + dto.getAgentId()));

        if (agent.getRole() != Role.ROLE_AGENT) {
            throw new InvalidStatusTransitionException("Assigned user must have the COLLECTION_AGENT role");
        }

        User admin = securityUtils.getCurrentAuthenticatedUser();
        req.setAssignedAgent(agent);
        if (dto.getScheduledPickupDate() != null) {
            req.setPreferredPickupDate(dto.getScheduledPickupDate());
        }

        RequestStatus previous = req.getStatus();
        req.addStatusHistory(
                previous,
                RequestStatus.PICKUP_SCHEDULED,
                admin,
                admin.getFullName(),
                "Assigned to collection agent: " + agent.getFullName() +
                        (dto.getScheduledPickupDate() != null ? " for pickup on " + dto.getScheduledPickupDate() : "")
        );

        CollectionRequest saved = requestRepository.save(req);
        return CollectionRequestResponseDto.fromEntity(saved);
    }

    @Transactional
    public CollectionRequestResponseDto assignFacility(Long id, AssignFacilityDto dto) {
        CollectionRequest req = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection request not found with ID: " + id));

        RecyclingFacility facility = facilityRepository.findById(dto.getFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Recycling facility not found with ID: " + dto.getFacilityId()));

        User admin = securityUtils.getCurrentAuthenticatedUser();
        req.setAssignedFacility(facility);

        req.addStatusHistory(
                req.getStatus(),
                req.getStatus(),
                admin,
                admin.getFullName(),
                "Assigned to processing facility: " + facility.getName()
        );

        CollectionRequest saved = requestRepository.save(req);
        return CollectionRequestResponseDto.fromEntity(saved);
    }

    @Transactional
    public CollectionRequestResponseDto adminUpdateStatus(Long id, UpdateStatusDto dto) {
        CollectionRequest req = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection request not found with ID: " + id));

        User admin = securityUtils.getCurrentAuthenticatedUser();
        RequestStatus previous = req.getStatus();

        if (dto.getActualWeightKg() != null) {
            req.setActualWeightKg(dto.getActualWeightKg());
        }
        if (dto.getActualPickupDate() != null) {
            req.setActualPickupDate(dto.getActualPickupDate());
        }

        req.addStatusHistory(
                previous,
                dto.getNewStatus(),
                admin,
                admin.getFullName(),
                dto.getRemarks() != null ? dto.getRemarks() : "Status updated by Administrator"
        );

        CollectionRequest saved = requestRepository.save(req);
        return CollectionRequestResponseDto.fromEntity(saved);
    }
}
