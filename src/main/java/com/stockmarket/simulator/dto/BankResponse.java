package com.stockmarket.simulator.dto;

import java.util.List;

public record BankResponse(
        List<StockItem> stocks
) {
    public record StockItem(String name, long quantity) {}
}