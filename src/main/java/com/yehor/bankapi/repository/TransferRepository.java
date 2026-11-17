package com.yehor.bankapi.repository;

import com.yehor.bankapi.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<Transfer, Integer> {
}
