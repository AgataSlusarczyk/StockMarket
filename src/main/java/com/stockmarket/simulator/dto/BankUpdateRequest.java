package com.stockmarket.simulator.dto;

import java.util.List;

/**
 * Request for updating Bank state.
 */
public record BankUpdateRequest(
        List<StockItem> stocks
) {
    /**
     * Stock definition.
     */
    public record StockItem(String name, long quantity) {
    }
}