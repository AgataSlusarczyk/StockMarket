package com.stockmarket.simulator.model;

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
    private String walletId;
    private String stockName;
}
