package com.vittig.spring_digital_wallet.data.repository;

import com.vittig.spring_digital_wallet.data.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
    @Query("SELECT l FROM LedgerEntry l WHERE l.wallet.id = :walletId")
    List<LedgerEntry> getAllEntriesByWallet(Long walletId);

    @Query("SELECT l FROM LedgerEntry l WHERE l.transfer.id = :id AND " +
            "(l.wallet.id = :walletId)")
    List<LedgerEntry> getAllEntriesByTransfer(Long id, Long walletId);
}
