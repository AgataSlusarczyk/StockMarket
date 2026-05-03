package com.stockmarket.simulator.controller;

import com.stockmarket.simulator.dto.AuditLogResponse;
import com.stockmarket.simulator.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides access to the audit log.
 * <p>
 * Returns all successful wallet operations in order of occurrence.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
public class LogAuditController {
    private final AuditLogRepository auditLogRepository;

    /**
     * Returns full audit log.
     *
     * @return ordered list of logged operations
     */
    @GetMapping
    public ResponseEntity<AuditLogResponse> getLog() {
        var log = auditLogRepository.findAllByOrderByIdAsc()
                .stream()
                .map(entry -> new AuditLogResponse.LogItem(
                        entry.getType(),
                        entry.getWalletId(),
                        entry.getStockName()
                ))
                .toList();
        return ResponseEntity.ok(new AuditLogResponse(log));
    }
}
