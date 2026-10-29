package com.retech.controller;

import com.retech.dto.ApiResponse;
import com.retech.dto.CollectionRequestResponseDto;
import com.retech.dto.CreateCollectionRequestDto;
import com.retech.service.CollectionRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class CollectionRequestController {

    private final CollectionRequestService requestService;

    public CollectionRequestController(CollectionRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CollectionRequestResponseDto>> createRequest(@Valid @RequestBody CreateCollectionRequestDto dto) {
        CollectionRequestResponseDto response = requestService.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("E-waste pickup request submitted successfully! Your tracking code is: " + response.getTrackingCode(), response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CollectionRequestResponseDto>>> getMyRequests() {
        List<CollectionRequestResponseDto> list = requestService.getCustomerRequests();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CollectionRequestResponseDto>> getRequestById(@PathVariable Long id) {
        CollectionRequestResponseDto req = requestService.getRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(req));
    }

    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<ApiResponse<CollectionRequestResponseDto>> trackRequest(@PathVariable String trackingCode) {
        CollectionRequestResponseDto req = requestService.getByTrackingCode(trackingCode);
        return ResponseEntity.ok(ApiResponse.success(req));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<CollectionRequestResponseDto>> cancelRequest(@PathVariable Long id,
                                                                                  @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        CollectionRequestResponseDto cancelled = requestService.cancelRequest(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Request has been cancelled successfully", cancelled));
    }
}
