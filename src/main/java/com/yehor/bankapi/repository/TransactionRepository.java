package com.yehor.bankapi.repository;

import com.yehor.bankapi.model.Transaction;
import com.yehor.bankapi.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByAccount_Id(Integer accountId);

    List<Transaction> findByAccount_IdAndTransactionType(
            Integer accountId,
            TransactionType transactionType
    );
    List<Transaction> findByAccount_IdOrderByAmountAsc(Integer accountId);

    List<Transaction> findByAccount_IdOrderByAmountDesc(Integer accountId);
}

