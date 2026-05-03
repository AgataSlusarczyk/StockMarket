package com.stockmarket.simulator.controller;

import com.stockmarket.simulator.dto.BankResponse;
import com.stockmarket.simulator.dto.BankUpdateRequest;
import com.stockmarket.simulator.model.BankStock;
import com.stockmarket.simulator.repository.BankStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * REST controller responsible for managing the state of the Bank.
 * <p>
 * The Bank acts as the sole liquidity provider in the system and stores
 * the total available quantity of each stock.
 * <p>
 * This controller allows:
 * - retrieving the current state of available stocks
 * - overwriting the entire bank state
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks")
public class BankController {
    private final BankStockRepository bankStockRepository;

    /**
     * Retrieves the current state of all stocks available in the Bank.
     *
     * @return {@link ResponseEntity} containing {@link BankResponse}
     * with a list of all stocks and their quantities.
     */
    @GetMapping
    public ResponseEntity<BankResponse> getBankState() {
        var stocks = bankStockRepository.findAll()
                .stream()
                .map(bs -> new BankResponse.StockItem(bs.getName(), bs.getQuantity()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new BankResponse(stocks));
    }

    /**
     * Replaces the entire state of the Bank with a new set of stocks.
     * <p>
     * This operation:
     * - removes all existing stocks from the Bank
     * - inserts the provided list of stocks with their quantities
     * <p>
     * The operation is executed within a transaction to ensure consistency.
     *
     * @param bankUpdateRequest request containing the new list of stocks
     * @return {@link ResponseEntity} with HTTP 200 if the operation succeeds
     */
    @PostMapping
    @Transactional
    public ResponseEntity<Void> setBankState(@RequestBody BankUpdateRequest bankUpdateRequest) {
        bankStockRepository.deleteAll();

        var entities = bankUpdateRequest.stocks().stream()
                .map(stock -> new BankStock(stock.name(), stock.quantity()))
                .collect(Collectors.toList());

        bankStockRepository.saveAll(entities);

        return ResponseEntity.ok().build();
    }
}

