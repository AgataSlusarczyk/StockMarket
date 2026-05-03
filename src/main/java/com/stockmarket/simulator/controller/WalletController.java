package com.stockmarket.simulator.controller;

import com.stockmarket.simulator.dto.StockOperationRequest;
import com.stockmarket.simulator.dto.WalletResponse;
import com.stockmarket.simulator.exception.NotFoundException;
import com.stockmarket.simulator.model.WalletStockId;
import com.stockmarket.simulator.repository.WalletRepository;
import com.stockmarket.simulator.repository.WalletStockRepository;
import com.stockmarket.simulator.service.StockExchangeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * Manages wallet operations and state.
 * <p>
 * Supports stock buy/sell operations and wallet queries.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/wallets")
public class WalletController {
    private final StockExchangeService service;
    private final WalletStockRepository walletStockRepository;
    private final WalletRepository walletRepository;

    /**
     * Executes buy or sell operation on a wallet.
     *
     * @return HTTP 200 on success
     */
    @PostMapping("/{walletId}/stocks/{stockName}")
    public ResponseEntity<Void> operate(@PathVariable String walletId, @PathVariable String stockName, @RequestBody StockOperationRequest request) throws BadRequestException, NotFoundException {
        service.operate(walletId, stockName, request.type());
        return ResponseEntity.ok().build();
    }

    /**
     *
     * Returns wallet state with all owned stocks.
     *
     * @return wallet details or 404 if not found
     */
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable String walletId) {

        if (!walletRepository.existsById(walletId)) {
            return ResponseEntity.notFound().build();
        }

        var stocks = walletStockRepository.findByIdWalletId(walletId).stream().map(ws -> new WalletResponse.StockItem(ws.getId().getStockName(), ws.getQuantity())).collect(Collectors.toList());

        return ResponseEntity.ok(new WalletResponse(walletId, stocks));
    }

    /**
     * Returns quantity of a specific stock in a wallet.
     *
     * @return stock quantity (0 if not present)
     */
    @GetMapping("/{walletId}/stocks/{stockName}")
    public ResponseEntity<Long> getWalletStock(@PathVariable String walletId, @PathVariable String stockName) {
        var id = new WalletStockId(walletId, stockName);

        return walletStockRepository.findById(id).map(ws -> ResponseEntity.ok(ws.getQuantity())).orElse(ResponseEntity.ok(0L));
    }
}
