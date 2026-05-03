package com.stockmarket.simulator.model;

import com.stockmarket.simulator.exception.BadRequestException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stock holding inside a Wallet.
 * <p>
 * Composite key: walletId + stockName.
 */
@Entity
@Table(name = "wallet_stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class WalletStock {
    @EmbeddedId
    private WalletStockId id;

    private long quantity;

    public void increaseQuantity() {
        this.quantity += 1;
    }

    public void decreaseQuantity() {
        if (this.quantity > 0) {
            this.quantity -= 1;
        } else {
            throw new BadRequestException("No stock in wallet");
        }
    }


}
