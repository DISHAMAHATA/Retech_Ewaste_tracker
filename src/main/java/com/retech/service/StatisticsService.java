package com.retech.service;

import com.retech.dto.CustomerImpactDto;
import com.retech.dto.SystemStatisticsDto;
import com.retech.entity.CollectionRequest;
import com.retech.entity.User;
import com.retech.entity.enums.RequestStatus;
import com.retech.repository.*;
import com.retech.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    // Documented CO2e offset benchmark factor: 1.44 kg CO2e saved per 1 kg of certified e-waste recycled (EPA/UN WEEE studies)
    private static final BigDecimal CO2_SAVINGS_PER_KG = new BigDecimal("1.44");

    private static final List<RequestStatus> COLLECTED_OR_LATER_STATUSES = List.of(
            RequestStatus.COLLECTED,
            RequestStatus.RECEIVED_AT_FACILITY,
            RequestStatus.UNDER_PROCESSING,
            RequestStatus.RECYCLING_COMPLETED
    );

    private static final List<RequestStatus> RECYCLED_STATUSES = List.of(
            RequestStatus.RECYCLING_COMPLETED
    );

    private static final List<RequestStatus> PENDING_STATUSES = List.of(
            RequestStatus.REQUEST_SUBMITTED,
            RequestStatus.REQUEST_APPROVED,
            RequestStatus.PICKUP_SCHEDULED
    );

    private static final List<RequestStatus> CANCELLED_OR_REJECTED = List.of(
            RequestStatus.CANCELLED,
            RequestStatus.REQUEST_REJECTED
    );

    private final CollectionRequestRepository requestRepository;
    private final ProcessingRecordRepository processingRecordRepository;
    private final UserRepository userRepository;
    private final CollectionCentreRepository centreRepository;
    private final RecyclingFacilityRepository facilityRepository;
    private final SecurityUtils securityUtils;

    public StatisticsService(CollectionRequestRepository requestRepository,
                             ProcessingRecordRepository processingRecordRepository,
                             UserRepository userRepository,
                             CollectionCentreRepository centreRepository,
                             RecyclingFacilityRepository facilityRepository,
                             SecurityUtils securityUtils) {
        this.requestRepository = requestRepository;
        this.processingRecordRepository = processingRecordRepository;
        this.userRepository = userRepository;
        this.centreRepository = centreRepository;
        this.facilityRepository = facilityRepository;
        this.securityUtils = securityUtils;
    }

    public CustomerImpactDto getCustomerImpactStatistics() {
        User customer = securityUtils.getCurrentAuthenticatedUser();
        Long customerId = customer.getId();

        long totalSubmitted = requestRepository.countByCustomerId(customerId);
        long totalCollected = requestRepository.countByCustomerIdAndStatusIn(customerId, COLLECTED_OR_LATER_STATUSES);
        long totalRecycled = requestRepository.countByCustomerIdAndStatusIn(customerId, RECYCLED_STATUSES);
        long totalPending = requestRepository.countByCustomerIdAndStatusIn(customerId, PENDING_STATUSES);

        BigDecimal totalWeightCollected = requestRepository.sumWeightByCustomerIdAndStatusIn(customerId, COLLECTED_OR_LATER_STATUSES);
        BigDecimal totalWeightRecycled = requestRepository.sumWeightByCustomerIdAndStatusIn(customerId, RECYCLED_STATUSES);

        BigDecimal co2Avoided = totalWeightRecycled.multiply(CO2_SAVINGS_PER_KG).setScale(2, RoundingMode.HALF_UP);

        return new CustomerImpactDto(
                totalSubmitted,
                totalCollected,
                totalRecycled,
                totalPending,
                totalWeightCollected.setScale(2, RoundingMode.HALF_UP),
                totalWeightRecycled.setScale(2, RoundingMode.HALF_UP),
                co2Avoided
        );
    }

    public SystemStatisticsDto getSystemStatistics() {
        SystemStatisticsDto stats = new SystemStatisticsDto();

        stats.setTotalRequestsSubmitted(requestRepository.count());
        stats.setTotalRequestsCollected(requestRepository.countByStatusIn(COLLECTED_OR_LATER_STATUSES));
        stats.setTotalRequestsAtFacility(requestRepository.countByStatusIn(List.of(RequestStatus.RECEIVED_AT_FACILITY, RequestStatus.UNDER_PROCESSING)));
        stats.setTotalRequestsCompleted(requestRepository.countByStatusIn(RECYCLED_STATUSES));
        stats.setTotalRequestsPending(requestRepository.countByStatusIn(PENDING_STATUSES));
        stats.setTotalRequestsCancelledOrRejected(requestRepository.countByStatusIn(CANCELLED_OR_REJECTED));

        BigDecimal collectedWeight = requestRepository.sumWeightByStatusIn(COLLECTED_OR_LATER_STATUSES);
        stats.setTotalWeightCollectedKg(collectedWeight != null ? collectedWeight.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        BigDecimal recycledWeight = processingRecordRepository.sumTotalWeightRecycled();
        stats.setTotalWeightRecycledKg(recycledWeight != null ? recycledWeight.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        BigDecimal reusedWeight = processingRecordRepository.sumTotalWeightReused();
        stats.setTotalWeightReusedKg(reusedWeight != null ? reusedWeight.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        BigDecimal disposedWeight = processingRecordRepository.sumTotalWeightDisposed();
        stats.setTotalWeightDisposedKg(disposedWeight != null ? disposedWeight.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        stats.setTotalRegisteredUsers(userRepository.count());
        stats.setTotalActiveCentres(centreRepository.findByActiveTrue().size());
        stats.setTotalActiveFacilities(facilityRepository.findByActiveTrue().size());

        // Category breakdown
        Map<String, Long> categoryMap = new LinkedHashMap<>();
        for (Object[] row : requestRepository.getCategoryBreakdown()) {
            categoryMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.setRequestsByCategory(categoryMap);

        // Status counts
        Map<String, Long> statusMap = new LinkedHashMap<>();
        for (Object[] row : requestRepository.getStatusCounts()) {
            statusMap.put(row[0].toString(), (Long) row[1]);
        }
        stats.setRequestsByStatus(statusMap);

        return stats;
    }

    public String generateRequestsCsv() {
        List<CollectionRequest> requests = requestRepository.findAll();
        StringWriter sw = new StringWriter();
        sw.append("Tracking Code,Item Name,Category,Quantity,Estimated Weight (kg),Actual Weight (kg),Condition,Status,Customer Name,City,Pickup Date,Created At\n");

        for (CollectionRequest req : requests) {
            sw.append(escapeCsv(req.getTrackingCode())).append(",")
              .append(escapeCsv(req.getItemName())).append(",")
              .append(escapeCsv(req.getCategory().name())).append(",")
              .append(String.valueOf(req.getQuantity())).append(",")
              .append(req.getEstimatedWeightKg() != null ? req.getEstimatedWeightKg().toString() : "").append(",")
              .append(req.getActualWeightKg() != null ? req.getActualWeightKg().toString() : "").append(",")
              .append(escapeCsv(req.getItemCondition().name())).append(",")
              .append(escapeCsv(req.getStatus().name())).append(",")
              .append(escapeCsv(req.getCustomer() != null ? req.getCustomer().getFullName() : "")).append(",")
              .append(escapeCsv(req.getCity())).append(",")
              .append(req.getActualPickupDate() != null ? req.getActualPickupDate().toString() : (req.getPreferredPickupDate() != null ? req.getPreferredPickupDate().toString() : "")).append(",")
              .append(req.getCreatedAt() != null ? req.getCreatedAt().toString() : "").append("\n");
        }
        return sw.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
