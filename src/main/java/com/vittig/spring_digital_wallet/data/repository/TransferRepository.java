package com.vittig.spring_digital_wallet.data.repository;

import com.vittig.spring_digital_wallet.data.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("SELECT t FROM Transfer t WHERE " +
            "(t.fromIban =:iban OR t.toIban = :iban) " +
            "AND " +
            "(:minAmount IS NULL OR t.amount >= :minAmount) " +
            "AND " +
            "(:maxAmount IS NULL OR t.amount <= :maxAmount) " +
            "AND " +
            "(:fromDate IS NULL OR t.createdAt >= :fromDate) " +
            "AND " +
            "(:toDate IS NULL OR t.createdAt <= :toDate) " +
            "ORDER BY t.createdAt DESC, t.id ASC")
    List<Transfer> findTransfersForWallet(String iban, BigDecimal minAmount, BigDecimal maxAmount,
                                          LocalDateTime fromDate, LocalDateTime toDate);
}
