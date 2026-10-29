package com.retech.controller;

import com.retech.dto.ApiResponse;
import com.retech.dto.CollectionRequestResponseDto;
import com.retech.dto.CreateProcessingRecordDto;
import com.retech.dto.ProcessingRecordDto;
import com.retech.dto.UpdateStatusDto;
import com.retech.service.CollectionRequestService;
import com.retech.service.ProcessingRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facility")
public class FacilityController {

    private final CollectionRequestService requestService;
    private final ProcessingRecordService processingRecordService;

    public FacilityController(CollectionRequestService requestService,
                              ProcessingRecordService processingRecordService) {
        this.requestService = requestService;
        this.processingRecordService = processingRecordService;
    }

    @GetMapping("/requests/intake")
    public ResponseEntity<ApiResponse<List<CollectionRequestResponseDto>>> getAwaitingIntake() {
        List<CollectionRequestResponseDto> list = requestService.getRequestsAwaitingFacilityIntake();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/requests/{facilityId}")
    public ResponseEntity<ApiResponse<List<CollectionRequestResponseDto>>> getFacilityRequests(@PathVariable Long facilityId) {
        List<CollectionRequestResponseDto> list = requestService.getFacilityAssignedRequests(facilityId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PatchMapping("/requests/{id}/status")
    public ResponseEntity<ApiResponse<CollectionRequestResponseDto>> updateFacilityStatus(@PathVariable Long id,
                                                                                          @Valid @RequestBody UpdateStatusDto dto) {
        CollectionRequestResponseDto updated = requestService.facilityUpdateStatus(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Facility stage updated successfully", updated));
    }

    @PostMapping("/records")
    public ResponseEntity<ApiResponse<ProcessingRecordDto>> createProcessingRecord(@Valid @RequestBody CreateProcessingRecordDto dto) {
        ProcessingRecordDto record = processingRecordService.createRecord(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Processing evidence record logged successfully", record));
    }

    @GetMapping("/records/request/{requestId}")
    public ResponseEntity<ApiResponse<List<ProcessingRecordDto>>> getRecordsByRequest(@PathVariable Long requestId) {
        List<ProcessingRecordDto> list = processingRecordService.getRecordsByRequest(requestId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/records")
    public ResponseEntity<ApiResponse<List<ProcessingRecordDto>>> getAllProcessingRecords() {
        List<ProcessingRecordDto> list = processingRecordService.getAllRecords();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
