package com.stockmarket.simulator.dto;

import java.util.List;

public record AuditLogResponse(
        List<LogItem> log
) {
    public record LogItem(
            String type,
            String wallet_id,
            String stock_name
    ) {}
}