package com.retech.service;

import com.retech.dto.CreateProcessingRecordDto;
import com.retech.dto.ProcessingRecordDto;
import com.retech.entity.CollectionRequest;
import com.retech.entity.ProcessingRecord;
import com.retech.entity.RecyclingFacility;
import com.retech.entity.User;
import com.retech.entity.enums.ProcessingStage;
import com.retech.entity.enums.RequestStatus;
import com.retech.entity.enums.Role;
import com.retech.exception.ResourceNotFoundException;
import com.retech.exception.UnauthorizedAccessException;
import com.retech.repository.CollectionRequestRepository;
import com.retech.repository.ProcessingRecordRepository;
import com.retech.repository.RecyclingFacilityRepository;
import com.retech.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ProcessingRecordService {

    private final ProcessingRecordRepository processingRecordRepository;
    private final CollectionRequestRepository requestRepository;
    private final RecyclingFacilityRepository facilityRepository;
    private final SecurityUtils securityUtils;

    public ProcessingRecordService(ProcessingRecordRepository processingRecordRepository,
                                   CollectionRequestRepository requestRepository,
                                   RecyclingFacilityRepository facilityRepository,
                                   SecurityUtils securityUtils) {
        this.processingRecordRepository = processingRecordRepository;
        this.requestRepository = requestRepository;
        this.facilityRepository = facilityRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public ProcessingRecordDto createRecord(CreateProcessingRecordDto dto) {
        User operator = securityUtils.getCurrentAuthenticatedUser();
        if (operator.getRole() != Role.ROLE_OPERATOR && operator.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedAccessException("Only authorized facility operators and administrators can log processing records");
        }

        CollectionRequest request = requestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Collection request not found with ID: " + dto.getRequestId()));

        RecyclingFacility facility = facilityRepository.findById(dto.getFacilityId())
                .orElseThrow(() -> new ResourceNotFoundException("Recycling facility not found with ID: " + dto.getFacilityId()));

        ProcessingRecord record = new ProcessingRecord(
                request,
                facility,
                operator,
                dto.getStage(),
                dto.getQuantityReceived(),
                dto.getWeightReceivedKg(),
                dto.getWeightReusedKg(),
                dto.getWeightRecycledKg(),
                dto.getWeightDisposedKg(),
                dto.getOutcome(),
                dto.getVerificationNotes() != null ? dto.getVerificationNotes().trim() : null
        );

        // Synchronize Collection Request status based on stage
        if (dto.getStage() == ProcessingStage.RECEIVED && request.getStatus() != RequestStatus.RECEIVED_AT_FACILITY) {
            request.addStatusHistory(
                    request.getStatus(),
                    RequestStatus.RECEIVED_AT_FACILITY,
                    operator,
                    operator.getFullName(),
                    "Physical item received at facility: " + facility.getName()
            );
            request.setAssignedFacility(facility);
        } else if ((dto.getStage() == ProcessingStage.SORTING || dto.getStage() == ProcessingStage.DISASSEMBLY || dto.getStage() == ProcessingStage.RECOVERY)
                && request.getStatus() != RequestStatus.UNDER_PROCESSING) {
            request.addStatusHistory(
                    request.getStatus(),
                    RequestStatus.UNDER_PROCESSING,
                    operator,
                    operator.getFullName(),
                    "Item moved to facility processing stage: " + dto.getStage().getDescription()
            );
        } else if (dto.getStage() == ProcessingStage.COMPLETED && request.getStatus() != RequestStatus.RECYCLING_COMPLETED) {
            request.addStatusHistory(
                    request.getStatus(),
                    RequestStatus.RECYCLING_COMPLETED,
                    operator,
                    operator.getFullName(),
                    "Recycling processing finalized. Outcome: " + dto.getOutcome().getDescription()
            );
        }

        requestRepository.save(request);
        ProcessingRecord saved = processingRecordRepository.save(record);
        return ProcessingRecordDto.fromEntity(saved);
    }

    public List<ProcessingRecordDto> getRecordsByRequest(Long requestId) {
        return processingRecordRepository.findByCollectionRequestIdOrderByRecordedAtDesc(requestId).stream()
                .map(ProcessingRecordDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ProcessingRecordDto> getRecordsByFacility(Long facilityId) {
        return processingRecordRepository.findByFacilityIdOrderByRecordedAtDesc(facilityId).stream()
                .map(ProcessingRecordDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ProcessingRecordDto> getAllRecords() {
        return processingRecordRepository.findAll().stream()
                .map(ProcessingRecordDto::fromEntity)
                .collect(Collectors.toList());
    }
}
