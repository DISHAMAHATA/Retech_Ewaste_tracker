package com.retech.controller;

import com.retech.dto.ApiResponse;
import com.retech.dto.CollectionRequestResponseDto;
import com.retech.dto.UpdateStatusDto;
import com.retech.service.CollectionRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final CollectionRequestService requestService;

    public AgentController(CollectionRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/requests")
    public ResponseEntity<ApiResponse<List<CollectionRequestResponseDto>>> getAssignedPickups() {
        List<CollectionRequestResponseDto> list = requestService.getAgentAssignedRequests();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PatchMapping("/requests/{id}/status")
    public ResponseEntity<ApiResponse<CollectionRequestResponseDto>> updatePickupStatus(@PathVariable Long id,
                                                                                        @Valid @RequestBody UpdateStatusDto dto) {
        CollectionRequestResponseDto updated = requestService.agentUpdateStatus(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Pickup status updated successfully", updated));
    }
}
