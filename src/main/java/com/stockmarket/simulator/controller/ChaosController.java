package com.stockmarket.simulator.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/chaos")
public class ChaosController {

    @PostMapping
    public ResponseEntity<Void> chaos() {
        log.warn("CHAOS requested – shutting down this instance NOW");

        // Osobny wątek żeby odpowiedź zdążyła wrócić do klienta
        Thread.ofVirtual().start(() -> {
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            Runtime.getRuntime().halt(1);
        });

        return ResponseEntity.ok().build();
    }
}