package com.vittig.spring_digital_wallet.data.repository;

import com.vittig.spring_digital_wallet.data.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    @Query("SELECT t FROM Transfer t WHERE t.toIban = :iban OR t.fromIban = :iban " +
            "ORDER BY t.createdAt DESC, t.id ASC")
    List<Transfer> findTransfersForCurrentUser(String iban);
}
