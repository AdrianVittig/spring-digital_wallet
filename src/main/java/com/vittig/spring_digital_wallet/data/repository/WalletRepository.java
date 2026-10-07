package com.vittig.spring_digital_wallet.data.repository;

import com.vittig.spring_digital_wallet.data.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("SELECT w FROM Wallet w WHERE w.id = :userId")
    Optional<Wallet> findCurrentWalletByUserId(Long userId);

    @Query("SELECT w FROM Wallet w WHERE w.user.email = :email")
    Optional<Wallet> findWalletByEmail(String email);

    @Query("SELECT w FROM Wallet w WHERE w.iban = :iban")
    Optional<Wallet> findWalletByIban(String iban);
}
