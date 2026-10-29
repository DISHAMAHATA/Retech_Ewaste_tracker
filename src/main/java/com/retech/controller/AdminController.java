package com.retech.controller;

import com.retech.dto.*;
import com.retech.entity.enums.EwasteCategory;
import com.retech.entity.enums.RequestStatus;
import com.retech.service.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CollectionRequestService requestService;
    private final UserService userService;
    private final CollectionCentreService centreService;
    private final RecyclingFacilityService facilityService;
    private final StatisticsService statisticsService;

    public AdminController(CollectionRequestService requestService,
                           UserService userService,
                           CollectionCentreService centreService,
                           RecyclingFacilityService facilityService,
                           StatisticsService statisticsService) {
        this.requestService = requestService;
        this.userService = userService;
        this.centreService = centreService;
        this.facilityService = facilityService;
        this.statisticsService = statisticsService;
    }

    // --- REQUESTS MANAGEMENT ---

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<Page<CollectionRequestResponseDto>>> getAllRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) EwasteCategory category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CollectionRequestResponseDto> results = requestService.searchAllRequests(status, category, search, pageable);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    @PostMapping("/requests/{id}/assign-agent")
    public ResponseEntity<ApiResponse<CollectionRequestResponseDto>> assignAgent(
            @PathVariable Long id,
            @Valid @RequestBody AssignAgentDto dto) {
        CollectionRequestResponseDto updated = requestService.assignAgent(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Collection agent assigned and pickup scheduled", updated));
    }

    @PostMapping("/requests/{id}/assign-facility")
    public ResponseEntity<ApiResponse<CollectionRequestResponseDto>> assignFacility(
            @PathVariable Long id,
            @Valid @RequestBody AssignFacilityDto dto) {
        CollectionRequestResponseDto updated = requestService.assignFacility(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Processing facility assigned successfully", updated));
    }

    @PatchMapping("/requests/{id}/status")
    public ResponseEntity<ApiResponse<CollectionRequestResponseDto>> updateRequestStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusDto dto) {
        CollectionRequestResponseDto updated = requestService.adminUpdateStatus(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Request status updated by administrator", updated));
    }

    // --- USER MANAGEMENT ---

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserDto>>> getAllUsers() {
        List<AdminUserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/agents")
    public ResponseEntity<ApiResponse<List<AdminUserDto>>> getAgents() {
        List<AdminUserDto> agents = userService.getActiveAgents();
        return ResponseEntity.ok(ApiResponse.success(agents));
    }

    @GetMapping("/operators")
    public ResponseEntity<ApiResponse<List<AdminUserDto>>> getOperators() {
        List<AdminUserDto> operators = userService.getActiveOperators();
        return ResponseEntity.ok(ApiResponse.success(operators));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<AdminUserDto>> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleDto dto) {
        AdminUserDto updated = userService.updateUserRoleAndStatus(id, dto);
        return ResponseEntity.ok(ApiResponse.success("User role / status updated successfully", updated));
    }

    // --- COLLECTION CENTRES CRUD ---

    @PostMapping("/centres")
    public ResponseEntity<ApiResponse<CollectionCentreDto>> createCentre(@Valid @RequestBody CollectionCentreDto dto) {
        CollectionCentreDto created = centreService.createCentre(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Collection centre created", created));
    }

    @PutMapping("/centres/{id}")
    public ResponseEntity<ApiResponse<CollectionCentreDto>> updateCentre(
            @PathVariable Long id,
            @Valid @RequestBody CollectionCentreDto dto) {
        CollectionCentreDto updated = centreService.updateCentre(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Collection centre updated", updated));
    }

    @DeleteMapping("/centres/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCentre(@PathVariable Long id) {
        centreService.deleteCentre(id);
        return ResponseEntity.ok(ApiResponse.success("Collection centre deactivated successfully", null));
    }

    // --- RECYCLING FACILITIES CRUD ---

    @GetMapping("/facilities")
    public ResponseEntity<ApiResponse<List<RecyclingFacilityDto>>> getAllFacilities() {
        List<RecyclingFacilityDto> facilities = facilityService.getAllActiveFacilities();
        return ResponseEntity.ok(ApiResponse.success(facilities));
    }

    @PostMapping("/facilities")
    public ResponseEntity<ApiResponse<RecyclingFacilityDto>> createFacility(@Valid @RequestBody RecyclingFacilityDto dto) {
        RecyclingFacilityDto created = facilityService.createFacility(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Recycling facility created", created));
    }

    @PutMapping("/facilities/{id}")
    public ResponseEntity<ApiResponse<RecyclingFacilityDto>> updateFacility(
            @PathVariable Long id,
            @Valid @RequestBody RecyclingFacilityDto dto) {
        RecyclingFacilityDto updated = facilityService.updateFacility(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Recycling facility updated", updated));
    }

    @DeleteMapping("/facilities/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFacility(@PathVariable Long id) {
        facilityService.deleteFacility(id);
        return ResponseEntity.ok(ApiResponse.success("Recycling facility deactivated successfully", null));
    }

    // --- STATISTICS & EXPORTS ---

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<SystemStatisticsDto>> getSystemStatistics() {
        SystemStatisticsDto stats = statisticsService.getSystemStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping(value = "/reports/export", produces = "text/csv")
    public ResponseEntity<String> exportRequestsCsv() {
        String csv = statisticsService.generateRequestsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"retech-requests-report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
