package com.stockmarket.simulator.dto;

import java.util.List;

/**
 * Response containing audit log entries.
 */
public record AuditLogResponse(
        List<LogItem> log
) {
    /**
     * Single audit log entry.
     */
    public record LogItem(
            String type,
            String wallet_id,
            String stock_name
    ) {
    }
}