package com.pixservice.infrastructure.persistence;

import com.pixservice.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> { }
