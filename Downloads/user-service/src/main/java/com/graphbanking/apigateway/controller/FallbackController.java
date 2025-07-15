package com.graphbanking.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/user")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "User Service is currently unavailable",
                        "message", "Please try again later",
                        "timestamp", LocalDateTime.now(),
                        "service", "user-service"
                )));
    }

    @PostMapping("/user")
    public Mono<ResponseEntity<Map<String, Object>>> userServiceFallbackPost() {
        return userServiceFallback();
    }

    @GetMapping("/account")
    public Mono<ResponseEntity<Map<String, Object>>> accountServiceFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Account Service is currently unavailable",
                        "message", "Please try again later",
                        "timestamp", LocalDateTime.now(),
                        "service", "account-service"
                )));
    }

    @PostMapping("/account")
    public Mono<ResponseEntity<Map<String, Object>>> accountServiceFallbackPost() {
        return accountServiceFallback();
    }

    @GetMapping("/transaction")
    public Mono<ResponseEntity<Map<String, Object>>> transactionServiceFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Transaction Service is currently unavailable",
                        "message", "Please try again later",
                        "timestamp", LocalDateTime.now(),
                        "service", "transaction-service"
                )));
    }

    @PostMapping("/transaction")
    public Mono<ResponseEntity<Map<String, Object>>> transactionServiceFallbackPost() {
        return transactionServiceFallback();
    }
} 