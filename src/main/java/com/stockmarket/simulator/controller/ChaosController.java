package com.stockmarket.simulator.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for simulating instance failure.
 * <p>
 * POST /chaos terminates this application instance.
 */
@Slf4j
@RestController
@RequestMapping("/chaos")
public class ChaosController {

    /**
     * Triggers shutdown of the current instance.
     *
     * @return HTTP 200 before termination
     */
    @PostMapping
    public ResponseEntity<Void> chaos() {
        log.warn("CHAOS requested – shutting down this instance NOW");

        Thread.ofVirtual().start(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            Runtime.getRuntime().halt(1);
        });

        return ResponseEntity.ok().build();
    }
}