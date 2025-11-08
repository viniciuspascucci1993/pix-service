package com.pixservice.infrastructure.persistence;

import com.pixservice.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByEndToEndId(String endToEndId);
}
