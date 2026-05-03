package com.stockmarket.simulator.repository;

import com.stockmarket.simulator.model.BankStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Bank stock entities.
 */
@Repository
public interface BankStockRepository extends JpaRepository<BankStock, String> {

    /**
     * Locks and retrieves a stock for update.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BankStock b where b.name = :name")
    Optional<BankStock> findForUpdate(@Param("name") String name);
}
