package com.stockmarket.simulator.service;

import com.stockmarket.simulator.exception.NotFoundException;
import com.stockmarket.simulator.model.*;
import com.stockmarket.simulator.repository.AuditLogRepository;
import com.stockmarket.simulator.repository.BankStockRepository;
import com.stockmarket.simulator.repository.WalletRepository;
import com.stockmarket.simulator.repository.WalletStockRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
    import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class StockExchangeService {
    private final BankStockRepository bankStockRepository;
    private final WalletStockRepository walletStockRepository;
    private final WalletRepository walletRepository;
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void operate(String walletId, String stockName, String type) throws BadRequestException {
        switch (type) {
            case "buy" -> buy(walletId, stockName);
            case "sell" -> sell(walletId, stockName);
            default -> throw new BadRequestException("Invalid type");
        }
    }

    private void buy(final String walletId, final String stockName) throws BadRequestException {
        BankStock bankStock = bankStockRepository.findForUpdate(stockName).orElseThrow(NotFoundException::new);
        if(bankStock.getQuantity() == 0){
            throw new BadRequestException("No stock in bank");
        }
        bankStock.decreaseQuantity();

        walletRepository.findById(walletId).orElseGet(() -> walletRepository.save(new Wallet(walletId)));

        WalletStock walletStock = walletStockRepository
                .findById(new WalletStockId(walletId, stockName))
                .orElse(new WalletStock(
                        new WalletStockId(walletId, stockName)
                        ,0
                ));

        walletStock.increaseQuantity();
        walletStockRepository.save(walletStock);

        auditLogRepository.save(new AuditLogEntry(null, "buy", walletId, stockName, Instant.now()));

    }

    private void sell(final String walletId, final String stockName) throws BadRequestException {
        WalletStock walletStock = walletStockRepository.findForUpdate(walletId, stockName).orElseThrow(NotFoundException::new);
        if(walletStock.getQuantity() == 0){
            throw new BadRequestException("No stock in wallet");
        }

        walletStock.decreaseQuantity();

        BankStock bankStock = bankStockRepository
                .findForUpdate(stockName).orElseThrow(NotFoundException::new);

        bankStock.increaseQuantity();

        auditLogRepository.save(new AuditLogEntry(null, "sell", walletId, stockName, Instant.now()));
    }
}
