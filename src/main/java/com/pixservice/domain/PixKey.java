package com.pixservice.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "TB_PIX_KEYS", uniqueConstraints = {
        @UniqueConstraint(columnNames = "keyValue")
})
public class PixKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyValue; // valor da chave pix

    @Column(nullable = false)
    private String keyType; // tipo da chave

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public PixKey() { }

    public PixKey(Long id, String keyValue, String keyType, Wallet wallet, Instant createdAt) {
        this.id = id;
        this.keyValue = keyValue;
        this.keyType = keyType;
        this.wallet = wallet;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
