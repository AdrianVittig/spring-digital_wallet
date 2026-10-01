package com.vittig.spring_digital_wallet.data.repository;

import com.vittig.spring_digital_wallet.data.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {
}
