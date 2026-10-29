package com.retech.controller;

import com.retech.dto.ApiResponse;
import com.retech.dto.CollectionCentreDto;
import com.retech.service.CollectionCentreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centres")
public class CollectionCentreController {

    private final CollectionCentreService centreService;

    public CollectionCentreController(CollectionCentreService centreService) {
        this.centreService = centreService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CollectionCentreDto>>> getCentres(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category) {
        List<CollectionCentreDto> list = centreService.searchCentres(city, category);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/cities")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableCities() {
        List<String> cities = centreService.getAvailableCities();
        return ResponseEntity.ok(ApiResponse.success(cities));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CollectionCentreDto>> getCentreById(@PathVariable Long id) {
        CollectionCentreDto centre = centreService.getCentreById(id);
        return ResponseEntity.ok(ApiResponse.success(centre));
    }
}
