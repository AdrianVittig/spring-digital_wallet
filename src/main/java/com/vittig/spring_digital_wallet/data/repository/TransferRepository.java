package com.vittig.spring_digital_wallet.data.repository;

import com.vittig.spring_digital_wallet.data.entity.Transfer;
import com.vittig.spring_digital_wallet.data.util.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("SELECT t FROM Transfer t WHERE t.fromWallet.id = :walletId " +
            "OR t.toWallet.id = :walletId")
    List<Transfer> getAllTransfers(Long walletId);

    @Query("SELECT t FROM Transfer t WHERE t.id = :id AND " +
            "(t.fromWallet.id = :walletId OR t.toWallet.id = :walletId)")
    Optional<Transfer> getTransferById(Long walletId, Long id);

    @Query("SELECT t FROM Transfer t WHERE " +
            "(t.fromWallet.id = :walletId OR t.toWallet.id = :walletId) " +
            "AND " +
            "(:startDate IS NULL OR t.createdAt >= :startDate) " +
            "AND " +
            "(:endDate IS NULL OR t.createdAt <= :endDate) " +
            "AND " +
            "(:minAmount IS NULL OR t.amount >= :minAmount) " +
            "AND " +
            "(:maxAmount IS NULL OR t.amount <= :maxAmount) " +
            "AND " +
            "(:status IS NULL OR t.status = :status)")
    List<Transfer> filter(Long walletId, BigDecimal minAmount, BigDecimal maxAmount,
                          LocalDateTime startDate, LocalDateTime endDate,
                          TransferStatus status);
}
