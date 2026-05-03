package com.stockmarket.simulator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents stock available in the Bank.
 * <p>
 * Bank is the single liquidity provider.
 */
@Entity
@Table(name = "bank_stocks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankStock {
    @Id
    private String name;
    private long quantity;

    public void increaseQuantity() {
        this.quantity += 1;
    }

    public void decreaseQuantity() {
        if (this.quantity > 0) {
            this.quantity -= 1;
        } else {
            throw new RuntimeException("No stock in bank");
        }
    }

}
