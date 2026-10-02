package com.vittig.spring_digital_wallet.data.repository;

import com.vittig.spring_digital_wallet.data.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("SELECT t FROM Transfer t WHERE t.fromWallet.id = :walletId " +
            "OR t.toWallet.id = :walletId")
    List<Transfer> getAllTransfers(Long walletId);

    @Query("SELECT t FROM Transfer t WHERE t.id = :id AND " +
            "(t.fromWallet.id = :walletId OR t.toWallet.id = :walletId)")
    Optional<Transfer> getTransferById(Long walletId, Long id);
}
