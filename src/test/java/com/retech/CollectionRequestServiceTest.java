package com.retech;

import com.retech.dto.CollectionRequestResponseDto;
import com.retech.dto.CreateCollectionRequestDto;
import com.retech.entity.CollectionRequest;
import com.retech.entity.User;
import com.retech.entity.enums.EwasteCategory;
import com.retech.entity.enums.ItemCondition;
import com.retech.entity.enums.RequestStatus;
import com.retech.entity.enums.Role;
import com.retech.exception.InvalidStatusTransitionException;
import com.retech.exception.UnauthorizedAccessException;
import com.retech.repository.CollectionRequestRepository;
import com.retech.repository.UserRepository;
import com.retech.service.CollectionRequestService;
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
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CollectionRequestServiceTest {

    @Autowired
    private CollectionRequestService requestService;

    @Autowired
    private CollectionRequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private User customer1;
    private User customer2;

    @BeforeEach
    void setUp() {
        requestRepository.deleteAll();
        userRepository.deleteAll();

        customer1 = new User("cust_one", "cust1@example.com", "hash", "Customer One", "9876543201", "Addr 1", "City A", "100001", Role.ROLE_CUSTOMER);
        customer1 = userRepository.save(customer1);

        customer2 = new User("cust_two", "cust2@example.com", "hash", "Customer Two", "9876543202", "Addr 2", "City B", "100002", Role.ROLE_CUSTOMER);
        customer2 = userRepository.save(customer2);
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
    @DisplayName("Should generate unique tracking codes matching RET-YYYY-XXXXXXXX pattern")
    void testTrackingCodeGeneration() {
        Set<String> generated = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            String code = requestService.generateUniqueTrackingCode();
            assertTrue(code.matches("^RET-202[0-9]-[2-9A-HJ-NP-Z]{8}$"), "Code pattern invalid: " + code);
            assertTrue(generated.add(code), "Duplicate tracking code generated: " + code);
        }
    }

    @Test
    @DisplayName("Customer should successfully submit request with initial REQUEST_SUBMITTED status")
    void testCreateCollectionRequest() {
        authenticateAs(customer1);

        CreateCollectionRequestDto dto = new CreateCollectionRequestDto();
        dto.setItemName("MacBook Pro 13");
        dto.setCategory(EwasteCategory.LAPTOPS_COMPUTERS);
        dto.setQuantity(1);
        dto.setEstimatedWeightKg(new BigDecimal("1.50"));
        dto.setItemCondition(ItemCondition.PARTIALLY_WORKING);
        dto.setPickupAddress("Flat 101, Green Heights");
        dto.setCity("Metro City");
        dto.setPostalCode("560001");
        dto.setPreferredPickupDate(LocalDate.now().plusDays(2));
        dto.setNotes("Keyboard sticky");
        dto.setAccurateInfoConfirmed(true);

        CollectionRequestResponseDto result = requestService.createRequest(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertTrue(result.getTrackingCode().startsWith("RET-"));
        assertEquals("MacBook Pro 13", result.getItemName());
        assertEquals(RequestStatus.REQUEST_SUBMITTED, result.getStatus());
        assertEquals(1, result.getStatusHistory().size());
        assertEquals("Customer One", result.getStatusHistory().get(0).getChangedByName());
    }

    @Test
    @DisplayName("Customer should be able to cancel an eligible pending request")
    void testCancelEligibleRequest() {
        authenticateAs(customer1);

        CreateCollectionRequestDto dto = new CreateCollectionRequestDto();
        dto.setItemName("Broken Monitor");
        dto.setCategory(EwasteCategory.TELEVISIONS_DISPLAYS);
        dto.setQuantity(1);
        dto.setItemCondition(ItemCondition.NON_WORKING);
        dto.setPickupAddress("Street 5");
        dto.setCity("Metro City");
        dto.setPostalCode("560001");
        dto.setAccurateInfoConfirmed(true);

        CollectionRequestResponseDto created = requestService.createRequest(dto);

        CollectionRequestResponseDto cancelled = requestService.cancelRequest(created.getId(), "No longer needed");
        assertEquals(RequestStatus.CANCELLED, cancelled.getStatus());
        assertFalse(cancelled.isCancellable());
    }

    @Test
    @DisplayName("Customer cannot access or cancel another customer's request")
    void testUnauthorizedCustomerAccessDenied() {
        // Customer 1 creates request
        authenticateAs(customer1);
        CreateCollectionRequestDto dto = new CreateCollectionRequestDto();
        dto.setItemName("Private Server");
        dto.setCategory(EwasteCategory.LAPTOPS_COMPUTERS);
        dto.setQuantity(1);
        dto.setItemCondition(ItemCondition.WORKING);
        dto.setPickupAddress("Secret Location");
        dto.setCity("Metro City");
        dto.setPostalCode("560001");
        dto.setAccurateInfoConfirmed(true);

        CollectionRequestResponseDto created = requestService.createRequest(dto);

        // Customer 2 tries to view it
        authenticateAs(customer2);
        assertThrows(UnauthorizedAccessException.class, () -> requestService.getRequestById(created.getId()));

        // Customer 2 tries to cancel it
        assertThrows(UnauthorizedAccessException.class, () -> requestService.cancelRequest(created.getId(), "Malicious cancel"));
    }

    @Test
    @DisplayName("Cannot cancel an already collected request")
    void testCannotCancelCollectedRequest() {
        authenticateAs(customer1);
        CreateCollectionRequestDto dto = new CreateCollectionRequestDto();
        dto.setItemName("Old Phone");
        dto.setCategory(EwasteCategory.MOBILE_TABLETS);
        dto.setQuantity(1);
        dto.setItemCondition(ItemCondition.NON_WORKING);
        dto.setPickupAddress("Street 1");
        dto.setCity("Metro City");
        dto.setPostalCode("560001");
        dto.setAccurateInfoConfirmed(true);

        CollectionRequestResponseDto created = requestService.createRequest(dto);

        // Manually move to COLLECTED in DB
        CollectionRequest entity = requestRepository.findById(created.getId()).orElseThrow();
        entity.setStatus(RequestStatus.COLLECTED);
        requestRepository.save(entity);

        assertThrows(InvalidStatusTransitionException.class, () -> requestService.cancelRequest(created.getId(), "Cancel attempt"));
    }
}
