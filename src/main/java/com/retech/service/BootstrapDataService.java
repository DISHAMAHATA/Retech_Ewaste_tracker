package com.retech.service;

import com.retech.entity.*;
import com.retech.entity.enums.*;
import com.retech.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BootstrapDataService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapDataService.class);

    private final UserRepository userRepository;
    private final CollectionCentreRepository centreRepository;
    private final RecyclingFacilityRepository facilityRepository;
    private final CollectionRequestRepository requestRepository;
    private final ProcessingRecordRepository processingRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${retech.bootstrap.admin.email:admin@retech.org}")
    private String adminEmail;

    @Value("${retech.bootstrap.admin.password:Admin@ReTech2026!}")
    private String adminPassword;

    @Value("${retech.bootstrap.admin.username:admin}")
    private String adminUsername;

    @Value("${retech.bootstrap.seed-demo:true}")
    private boolean seedDemo;

    public BootstrapDataService(UserRepository userRepository,
                                CollectionCentreRepository centreRepository,
                                RecyclingFacilityRepository facilityRepository,
                                CollectionRequestRepository requestRepository,
                                ProcessingRecordRepository processingRecordRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.centreRepository = centreRepository;
        this.facilityRepository = facilityRepository;
        this.requestRepository = requestRepository;
        this.processingRecordRepository = processingRecordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        bootstrapAdminUser();
        if (seedDemo) {
            bootstrapDemoData();
        }
    }

    private void bootstrapAdminUser() {
        if (!userRepository.existsByEmail(adminEmail) && !userRepository.existsByUsername(adminUsername)) {
            User admin = new User(
                    adminUsername,
                    adminEmail,
                    passwordEncoder.encode(adminPassword),
                    "System Administrator",
                    "+91-9000000001",
                    "ReTech Operations Center, Sector 1",
                    "Metro City",
                    "560001",
                    Role.ROLE_ADMIN
            );
            userRepository.save(admin);
            log.info("Secure initial Administrator account bootstrapped for email: {}", adminEmail);
        }
    }

    private void bootstrapDemoData() {
        // 1. Seed Demo Customer
        User customer = userRepository.findByEmail("customer@retech-demo.org").orElseGet(() -> {
            User u = new User(
                    "demo_customer",
                    "customer@retech-demo.org",
                    passwordEncoder.encode("DemoCustomer123!"),
                    "Ananya Sharma",
                    "+91-9888877701",
                    "Flat 302, Palm Meadows, Whitefield",
                    "Metro City",
                    "560066",
                    Role.ROLE_CUSTOMER
            );
            return userRepository.save(u);
        });

        // 2. Seed Demo Agent
        User agent = userRepository.findByEmail("agent@retech-demo.org").orElseGet(() -> {
            User u = new User(
                    "demo_agent",
                    "agent@retech-demo.org",
                    passwordEncoder.encode("DemoAgent123!"),
                    "Vikram Singh",
                    "+91-9888877702",
                    "Fleet Hub North, Industrial Area",
                    "Metro City",
                    "560058",
                    Role.ROLE_AGENT
            );
            return userRepository.save(u);
        });

        // 3. Seed Demo Facility Operator
        User operator = userRepository.findByEmail("operator@retech-demo.org").orElseGet(() -> {
            User u = new User(
                    "demo_operator",
                    "operator@retech-demo.org",
                    passwordEncoder.encode("DemoOperator123!"),
                    "Dr. Rajesh Verma",
                    "+91-9888877703",
                    "Eco-Industrial Park, Gate 2",
                    "Metro City",
                    "560100",
                    Role.ROLE_OPERATOR
            );
            return userRepository.save(u);
        });

        // 4. Ensure demo facility exists
        List<RecyclingFacility> facilities = facilityRepository.findAll();
        RecyclingFacility defaultFacility = facilities.isEmpty() ? null : facilities.get(0);

        // 5. Seed diverse sample requests if customer has none
        if (requestRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId()).isEmpty()) {
            // Request 1: RECYCLING_COMPLETED
            CollectionRequest r1 = new CollectionRequest(
                    "RET-2026-DEMO0001",
                    customer,
                    "Dell Inspiron 15 (2018)",
                    EwasteCategory.LAPTOPS_COMPUTERS,
                    1,
                    new BigDecimal("2.40"),
                    ItemCondition.NON_WORKING,
                    "Flat 302, Palm Meadows, Whitefield",
                    "Metro City",
                    "560066",
                    LocalDate.now().minusDays(14),
                    "Motherboard failure, battery swollen"
            );
            r1.setActualWeightKg(new BigDecimal("2.35"));
            r1.setActualPickupDate(LocalDate.now().minusDays(12));
            r1.setAssignedAgent(agent);
            r1.setAssignedFacility(defaultFacility);
            r1.addStatusHistory(null, RequestStatus.REQUEST_SUBMITTED, customer, customer.getFullName(), "Request submitted by user");
            r1.addStatusHistory(RequestStatus.REQUEST_SUBMITTED, RequestStatus.REQUEST_APPROVED, null, "System Admin", "Request verified and approved");
            r1.addStatusHistory(RequestStatus.REQUEST_APPROVED, RequestStatus.PICKUP_SCHEDULED, null, "System Admin", "Assigned to Vikram Singh for pickup");
            r1.addStatusHistory(RequestStatus.PICKUP_SCHEDULED, RequestStatus.COLLECTED, agent, agent.getFullName(), "Item collected safely, weighed 2.35 kg");
            if (defaultFacility != null) {
                r1.addStatusHistory(RequestStatus.COLLECTED, RequestStatus.RECEIVED_AT_FACILITY, operator, operator.getFullName(), "Item received at " + defaultFacility.getName());
                r1.addStatusHistory(RequestStatus.RECEIVED_AT_FACILITY, RequestStatus.UNDER_PROCESSING, operator, operator.getFullName(), "Sent to disassembly and recovery");
                r1.addStatusHistory(RequestStatus.UNDER_PROCESSING, RequestStatus.RECYCLING_COMPLETED, operator, operator.getFullName(), "Recycling completed with copper and ABS recovery");
            }
            CollectionRequest savedR1 = requestRepository.save(r1);

            // Add corresponding processing record for R1
            if (defaultFacility != null) {
                ProcessingRecord pr = new ProcessingRecord(
                        savedR1,
                        defaultFacility,
                        operator,
                        ProcessingStage.COMPLETED,
                        1,
                        new BigDecimal("2.35"),
                        new BigDecimal("0.30"), // Reused (chassis parts)
                        new BigDecimal("1.80"), // Recycled (copper, aluminum, ABS)
                        new BigDecimal("0.25"), // Disposed safely (hazardous epoxy residue)
                        ProcessingOutcome.MATERIAL_RECOVERY,
                        "Disassembled under certified ESD standard. Lead-free solder extracted."
                );
                processingRecordRepository.save(pr);
            }

            // Request 2: UNDER_PROCESSING
            CollectionRequest r2 = new CollectionRequest(
                    "RET-2026-DEMO0002",
                    customer,
                    "Samsung Galaxy S9 & Power Banks",
                    EwasteCategory.MOBILE_TABLETS,
                    3,
                    new BigDecimal("0.85"),
                    ItemCondition.OBSOLETE_SCRAP,
                    "Flat 302, Palm Meadows, Whitefield",
                    "Metro City",
                    "560066",
                    LocalDate.now().minusDays(5),
                    "Screen cracked on phone, power banks hold no charge"
            );
            r2.setActualWeightKg(new BigDecimal("0.90"));
            r2.setActualPickupDate(LocalDate.now().minusDays(3));
            r2.setAssignedAgent(agent);
            r2.setAssignedFacility(defaultFacility);
            r2.addStatusHistory(null, RequestStatus.REQUEST_SUBMITTED, customer, customer.getFullName(), "Request submitted by user");
            r2.addStatusHistory(RequestStatus.REQUEST_SUBMITTED, RequestStatus.PICKUP_SCHEDULED, null, "System Admin", "Pickup scheduled");
            r2.addStatusHistory(RequestStatus.PICKUP_SCHEDULED, RequestStatus.COLLECTED, agent, agent.getFullName(), "Collected from doorstep");
            if (defaultFacility != null) {
                r2.addStatusHistory(RequestStatus.COLLECTED, RequestStatus.RECEIVED_AT_FACILITY, operator, operator.getFullName(), "Intake verified at facility");
                r2.addStatusHistory(RequestStatus.RECEIVED_AT_FACILITY, RequestStatus.UNDER_PROCESSING, operator, operator.getFullName(), "Battery neutralization and shredding initiated");
            }
            requestRepository.save(r2);

            // Request 3: PICKUP_SCHEDULED
            CollectionRequest r3 = new CollectionRequest(
                    "RET-2026-DEMO0003",
                    customer,
                    "Mechanical Keyboard & HDMI Cables",
                    EwasteCategory.ACCESSORIES_PERIPHERALS,
                    4,
                    new BigDecimal("1.80"),
                    ItemCondition.PARTIALLY_WORKING,
                    "Flat 302, Palm Meadows, Whitefield",
                    "Metro City",
                    "560066",
                    LocalDate.now().plusDays(2),
                    "Key switches intact, cord frayed"
            );
            r3.setAssignedAgent(agent);
            r3.addStatusHistory(null, RequestStatus.REQUEST_SUBMITTED, customer, customer.getFullName(), "Request submitted by user");
            r3.addStatusHistory(RequestStatus.REQUEST_SUBMITTED, RequestStatus.REQUEST_APPROVED, null, "System Admin", "Request approved");
            r3.addStatusHistory(RequestStatus.REQUEST_APPROVED, RequestStatus.PICKUP_SCHEDULED, null, "System Admin", "Assigned to Vikram Singh for scheduled pickup");
            requestRepository.save(r3);

            // Request 4: REQUEST_SUBMITTED (Pending review)
            CollectionRequest r4 = new CollectionRequest(
                    "RET-2026-DEMO0004",
                    customer,
                    "LG 32-inch LED Television",
                    EwasteCategory.TELEVISIONS_DISPLAYS,
                    1,
                    new BigDecimal("6.50"),
                    ItemCondition.NON_WORKING,
                    "Flat 302, Palm Meadows, Whitefield",
                    "Metro City",
                    "560066",
                    LocalDate.now().plusDays(3),
                    "Backlight issue, powers on but black display"
            );
            r4.addStatusHistory(null, RequestStatus.REQUEST_SUBMITTED, customer, customer.getFullName(), "Initial collection request submitted");
            requestRepository.save(r4);

            log.info("Demonstration accounts and sample e-waste requests initialized successfully.");
        }
    }
}
