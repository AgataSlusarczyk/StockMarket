package com.stockmarket.simulator.dto;

import java.util.List;

/**
 * Response for Wallet state.
 */
public record WalletResponse(
        String id,
        List<StockItem> stocks
) {
    /**
     * Wallet stock item.
     */
    public record StockItem(String name, long quantity) {
    }
}