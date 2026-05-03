package com.stockmarket.simulator.repository;

import com.stockmarket.simulator.model.AuditLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for audit log entries.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Long> {

    /**
     * Returns all log entries ordered by creation order.
     */
    List<AuditLogEntry> findAllByOrderByIdAsc();
}