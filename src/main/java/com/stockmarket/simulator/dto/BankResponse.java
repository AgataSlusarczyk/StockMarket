package com.stockmarket.simulator.dto;

import java.util.List;

/**
 * Response for Bank state.
 */
public record BankResponse(
        List<StockItem> stocks
) {
    /**
     * Bank stock item.
     */
    public record StockItem(String name, long quantity) {
    }
}