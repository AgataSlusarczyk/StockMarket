package com.stockmarket.simulator.dto;

import java.util.List;

public record BankUpdateRequest(
        List<StockItem> stocks
) {
    public record StockItem(String name, long quantity) {}
}