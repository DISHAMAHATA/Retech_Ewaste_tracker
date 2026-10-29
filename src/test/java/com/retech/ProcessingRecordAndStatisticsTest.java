package com.retech;

import com.retech.dto.CreateProcessingRecordDto;
import com.retech.dto.CustomerImpactDto;
import com.retech.dto.ProcessingRecordDto;
import com.retech.dto.SystemStatisticsDto;
import com.retech.entity.CollectionRequest;
import com.retech.entity.RecyclingFacility;
import com.retech.entity.User;
import com.retech.entity.enums.*;
import com.retech.repository.*;
import com.retech.service.ProcessingRecordService;
import com.retech.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProcessingRecordAndStatisticsTest {

    @Autowired
    private ProcessingRecordService processingRecordService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private CollectionRequestRepository requestRepository;

    @Autowired
    private ProcessingRecordRepository processingRecordRepository;

    @Autowired
    private RecyclingFacilityRepository facilityRepository;

    @Autowired
    private UserRepository userRepository;

    private User operator;
    private User customer;
    private RecyclingFacility facility;
    private CollectionRequest request;

    @BeforeEach
    void setUp() {
        processingRecordRepository.deleteAll();
        requestRepository.deleteAll();
        facilityRepository.deleteAll();
        userRepository.deleteAll();

        customer = new User("cust_stat", "custstat@example.com", "hash", "Stat Customer", "9000000001", "Addr", "Metro City", "560001", Role.ROLE_CUSTOMER);
        customer = userRepository.save(customer);

        operator = new User("op_stat", "opstat@example.com", "hash", "Operator Joe", "9000000002", "Plant Addr", "Metro City", "560001", Role.ROLE_OPERATOR);
        operator = userRepository.save(operator);

        facility = new RecyclingFacility("Apex Eco Plant", "Industrial Zone 1", "Metro City", "560001", "9000000003", "apex@example.com", new BigDecimal("1000.00"), "Metals, ABS");
        facility = facilityRepository.save(facility);

        request = new CollectionRequest(
                "RET-2026-TEST0001",
                customer,
                "Old Laptop",
                EwasteCategory.LAPTOPS_COMPUTERS,
                1,
                new BigDecimal("2.50"),
                ItemCondition.NON_WORKING,
                "Doorstep 12",
                "Metro City",
                "560001",
                LocalDate.now(),
                null
        );
        request.setStatus(RequestStatus.COLLECTED);
        request.setActualWeightKg(new BigDecimal("2.40"));
        request = requestRepository.save(request);
    }

    private void authenticateAs(User user) {
        var auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Operator should create processing record and advance request to RECYCLING_COMPLETED")
    void testCreateProcessingRecordAndCompleteLifecycle() {
        authenticateAs(operator);

        CreateProcessingRecordDto dto = new CreateProcessingRecordDto();
        dto.setRequestId(request.getId());
        dto.setFacilityId(facility.getId());
        dto.setStage(ProcessingStage.COMPLETED);
        dto.setQuantityReceived(1);
        dto.setWeightReceivedKg(new BigDecimal("2.40"));
        dto.setWeightRecycledKg(new BigDecimal("1.80"));
        dto.setWeightReusedKg(new BigDecimal("0.40"));
        dto.setWeightDisposedKg(new BigDecimal("0.20"));
        dto.setOutcome(ProcessingOutcome.MATERIAL_RECOVERY);
        dto.setVerificationNotes("ESD dismantled, gold fingers stripped, ABS plastics granulated.");

        ProcessingRecordDto record = processingRecordService.createRecord(dto);

        assertNotNull(record);
        assertNotNull(record.getId());
        assertEquals("RET-2026-TEST0001", record.getTrackingCode());
        assertEquals(ProcessingStage.COMPLETED, record.getStage());
        assertEquals(ProcessingOutcome.MATERIAL_RECOVERY, record.getOutcome());

        // Verify request updated to RECYCLING_COMPLETED
        CollectionRequest updatedReq = requestRepository.findById(request.getId()).orElseThrow();
        assertEquals(RequestStatus.RECYCLING_COMPLETED, updatedReq.getStatus());
        assertTrue(updatedReq.getStatusHistory().size() >= 1);
    }

    @Test
    @DisplayName("Verify customer recycling impact statistics calculation")
    void testCustomerImpactCalculation() {
        // Complete the request
        request.setStatus(RequestStatus.RECYCLING_COMPLETED);
        request.setActualWeightKg(new BigDecimal("10.00"));
        requestRepository.save(request);

        authenticateAs(customer);

        CustomerImpactDto stats = statisticsService.getCustomerImpactStatistics();

        assertNotNull(stats);
        assertEquals(1, stats.getTotalRequestsSubmitted());
        assertEquals(1, stats.getTotalRequestsCollected());
        assertEquals(1, stats.getTotalRequestsRecycled());
        assertEquals(new BigDecimal("10.00"), stats.getTotalWeightRecycledKg());

        // 10.00 kg * 1.44 kg CO2e/kg = 14.40 kg CO2 avoided
        assertEquals(new BigDecimal("14.40"), stats.getEstimatedCo2AvoidedKg());
    }

    @Test
    @DisplayName("Verify CSV report generation contains expected headers and values")
    void testCsvReportGeneration() {
        String csv = statisticsService.generateRequestsCsv();
        assertNotNull(csv);
        assertTrue(csv.contains("Tracking Code,Item Name,Category,Quantity"));
        assertTrue(csv.contains("RET-2026-TEST0001"));
        assertTrue(csv.contains("Old Laptop"));
        assertTrue(csv.contains("Metro City"));
    }
}
