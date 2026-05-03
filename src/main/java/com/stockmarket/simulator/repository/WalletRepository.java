package com.stockmarket.simulator.repository;

import com.stockmarket.simulator.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Wallet entities.
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
}