package com.stockmarket.simulator.service;

import com.stockmarket.simulator.exception.BadRequestException;
import com.stockmarket.simulator.exception.NotFoundException;
import com.stockmarket.simulator.model.*;
import com.stockmarket.simulator.repository.AuditLogRepository;
import com.stockmarket.simulator.repository.BankStockRepository;
import com.stockmarket.simulator.repository.WalletRepository;
import com.stockmarket.simulator.repository.WalletStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional
public class StockExchangeService {
    private final BankStockRepository bankStockRepository;
    private final WalletStockRepository walletStockRepository;
    private final WalletRepository walletRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void operate(String walletId, String stockName, String type){
        switch (type) {
            case "buy" -> buy(walletId, stockName);
            case "sell" -> sell(walletId, stockName);
            default -> throw new BadRequestException("Invalid type");
        }
    }

    private void buy(final String walletId, final String stockName){
        BankStock bankStock = bankStockRepository.findForUpdate(stockName).orElseThrow(NotFoundException::new);
        if (bankStock.getQuantity() == 0) {
            throw new BadRequestException("No stock in bank");
        }

        walletRepository.findById(walletId)
                .orElseGet(() -> {
                    try {
                        return walletRepository.saveAndFlush(new Wallet(walletId));
                    } catch (DataIntegrityViolationException e) {
                        return walletRepository.findById(walletId).orElseThrow();
                    }
                });

        WalletStock walletStock = walletStockRepository
                .findForUpdate(walletId, stockName)
                .orElse(new WalletStock(
                        new WalletStockId(walletId, stockName), 0
                ));

        bankStock.decreaseQuantity();
        walletStock.increaseQuantity();

        walletStockRepository.save(walletStock);
        auditLogRepository.save(new AuditLogEntry(null, "buy", walletId, stockName, Instant.now()));
    }

    private void sell(final String walletId, final String stockName) {
        BankStock bankStock = bankStockRepository
                .findForUpdate(stockName)
                .orElseThrow(() -> new NotFoundException("Stock not found: " + stockName));

        if (!walletRepository.existsById(walletId)) {
            throw new NotFoundException("Wallet not found: " + walletId);
        }

        WalletStock walletStock = walletStockRepository
                .findForUpdate(walletId, stockName)
                .orElseThrow(() -> new BadRequestException("No stock in wallet"));

        if (walletStock.getQuantity() == 0) {
            throw new BadRequestException("No stock in wallet");
        }

        walletStock.decreaseQuantity();
        bankStock.increaseQuantity();

        walletStockRepository.save(walletStock);
        bankStockRepository.save(bankStock);

        auditLogRepository.save(new AuditLogEntry(null, "sell", walletId, stockName, Instant.now()));
    }
}