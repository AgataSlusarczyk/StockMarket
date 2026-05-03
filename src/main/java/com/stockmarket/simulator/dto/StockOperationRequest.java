package com.stockmarket.simulator.dto;

/**
 * Request for stock operation (buy/sell).
 */
public record StockOperationRequest(String type) {
}