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

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks")
public class BankController {
    private final BankStockRepository bankStockRepository;

    @GetMapping
    public ResponseEntity<BankResponse> getBankState() {
        var stocks = bankStockRepository.findAll()
                .stream()
                .map(bs -> new BankResponse.StockItem(bs.getName(), bs.getQuantity()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new BankResponse(stocks));
    }

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

