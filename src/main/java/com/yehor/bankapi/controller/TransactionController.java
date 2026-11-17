package com.yehor.bankapi.controller;

import com.yehor.bankapi.dto.response.TransactionResponse;
import com.yehor.bankapi.model.TransactionType;
import com.yehor.bankapi.service.BankService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    private final BankService bankService;

    public TransactionController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/{accountId}")
    public List<TransactionResponse> getTransactionByAccountId(@PathVariable Integer accountId) {
        return bankService.getTransactionsByAccountId(accountId)
                .stream()
                .map(transaction -> new TransactionResponse(
                transaction.getId(),
                transaction.getAccount().getId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getCreatedAt()
        ))
                .toList();
    }

    @GetMapping("/{accountId}/filter")
    public List<TransactionResponse> getTransactionByType(@PathVariable Integer accountId,@RequestParam("type") TransactionType transactionType){
        return bankService.getTransactionByType(accountId, transactionType)
                .stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getAccount().getId(),
                        transaction.getAmount(),
                        transaction.getTransactionType(),
                        transaction.getCreatedAt()
                ))
                .toList();
    }

    @GetMapping("/{accountId}/sorted")
    public List<TransactionResponse> getTransactionSortedByAmount(@PathVariable Integer accountId){
        return bankService.getTransactionsSortedByAmount(accountId)
                .stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getId(),
                        transaction.getAccount().getId(),
                        transaction.getAmount(),
                        transaction.getTransactionType(),
                        transaction.getCreatedAt()
                ))
                .toList();
    }
}
