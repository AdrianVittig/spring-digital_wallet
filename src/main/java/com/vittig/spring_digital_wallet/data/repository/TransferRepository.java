package com.vittig.spring_digital_wallet.data.repository;

import com.vittig.spring_digital_wallet.data.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
}
