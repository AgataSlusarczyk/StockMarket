# Stock Market Simulator

A simplified, highly available stock exchange simulation built with Java + Spring Boot.  
The system models a minimalistic stock market with wallets, a central bank (liquidity provider), and an audit log of user operations.

---

# System Overview

The system consists of three main components:

## Bank
- Central entity holding all available stock
- Sole liquidity provider
- Responsible for increasing/decreasing global stock supply

## Wallets
- User-owned stock containers
- Created automatically on first use
- Store quantities of individual stocks

## Audit Log
- Records only successful BUY/SELL operations
- Excludes all bank-level operations
- Preserves chronological order of events

---

# Key Assumptions
- Stock price is always 1 (fixed)
- No user balances or cash management
- No order book (instant execution model)
- Bank is the only source of liquidity
- Operations are atomic and transactional
- System is designed for high availability

---

# Running the Application

## Docker (recommended)
docker-compose up --build

## Local run (Linux / macOS)
./start.sh 8080

## Local run (Windows)
start.bat 8080

---

# Notes
Replace 8080 with any available port (XXXX).  
Application will be available at:
http://localhost:XXXX

---

# Tech Stack
- Java (latest LTS)
- Spring Boot
- Spring Data JPA
- PostgreSQL / H2 (depending on configuration)
- Docker + Docker Compose
- Nginx (reverse proxy / load balancing)
- Maven Wrapper (./mvnw)

---


# Architecture Notes

## Concurrency & Consistency
- Pessimistic locking used for:
    - Bank stock updates
    - Wallet stock updates
- Ensures correctness under concurrent operations

## Transaction Model
- Each buy/sell operation is fully transactional
- Either all changes succeed or none are applied

## Data Model
- BankStock – global stock availability
- Wallet – user container
- WalletStock – per-wallet holdings (composite key)
- AuditLogEntry – immutable event log

---

# Struktura projektu — Simulator

simulator/

├── src/                # Kod źródłowy aplikacji 
\
├── docker-compose.yml  # Konfiguracja wielu serwisów
\
├── Dockerfile          # Kontener aplikacji
\
├── nginx.conf          # Konfiguracja load balancera
\
├── start.sh            # Skrypt startowy Linux/macOS
\
├── start.bat           # Skrypt startowy Windows
\
├── pom.xml             # Build Maven
\
└── README.md           # Dokumentacja projektu