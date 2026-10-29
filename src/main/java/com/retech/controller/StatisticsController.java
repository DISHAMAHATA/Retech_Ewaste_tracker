package com.retech.controller;

import com.retech.dto.ApiResponse;
import com.retech.dto.CustomerImpactDto;
import com.retech.dto.SystemStatisticsDto;
import com.retech.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<CustomerImpactDto>> getCustomerImpact() {
        CustomerImpactDto stats = statisticsService.getCustomerImpactStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<SystemStatisticsDto>> getPublicStatistics() {
        SystemStatisticsDto stats = statisticsService.getSystemStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
