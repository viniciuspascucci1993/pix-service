package com.pixservice.infrastructure.persistence;

import com.pixservice.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByEndToEndId(String endToEndId);

    @Query("""
    SELECT COALESCE(SUM(
        CASE 
            WHEN t.toWallet.id = :walletId THEN t.amount 
            WHEN t.fromWallet.id = :walletId THEN -t.amount 
            ELSE 0 
        END
    ), 0)
    FROM Transaction t
    WHERE t.createdAt <= :at
      AND t.status = 'CONFIRMED'
""")
    Optional<BigDecimal> sumBalanceUntil(@Param("walletId") Long walletId, @Param("at") Instant at);
}
