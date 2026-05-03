package com.stockmarket.simulator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

/**
 * Composite key for WalletStock entity.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WalletStockId {
    @Column(name = "wallet_id")
    private String walletId;

    @Column(name = "stock_name")
    private String stockName;
}
