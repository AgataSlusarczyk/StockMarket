package com.stockmarket.simulator.repository;

import com.stockmarket.simulator.model.WalletStock;
import com.stockmarket.simulator.model.WalletStockId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Wallet stock holdings.
 */
public interface WalletStockRepository extends JpaRepository<WalletStock, WalletStockId> {

    /**
     * Locks and retrieves wallet stock for update.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select ws from WalletStock ws where ws.id.walletId = :walletId and ws.id.stockName = :stockName")
    Optional<WalletStock> findForUpdate(String walletId, String stockName);

    /**
     * Returns all stocks owned by wallet.
     */
    List<WalletStock> findByIdWalletId(String walletId);

}
