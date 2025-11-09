package com.pixservice.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "TB_IDEMPOTENCY_KEYS",
        uniqueConstraints = @UniqueConstraint(columnNames = {"wallet_id", "key_value"}))
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long walletId;

    @Column(nullable = false, unique = true)
    private String keyValue;

    @Column(nullable = false)
    private String responseBody;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public IdempotencyKey() { }

    public IdempotencyKey(Long walletId, String keyValue, String responseBody) {
        this.walletId = walletId;
        this.keyValue = keyValue;
        this.responseBody = responseBody;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
