package com.yehor.bankapi.controller;

import com.yehor.bankapi.dto.response.AccountResponse;
import com.yehor.bankapi.dto.request.*;
import com.yehor.bankapi.model.Account;
import com.yehor.bankapi.service.BankService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final BankService bankService;

    public AccountController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = bankService.getAllAccounts();
        return accounts.stream()
                .map(account -> new AccountResponse(
                        account.getId(),
                        account.getName(),
                        account.getLastName(),
                        account.getEmail(),
                        account.getBalance()
                ))
                .toList();
    }

    @GetMapping("/{idAcc}")
    public AccountResponse getAccount(@PathVariable Integer idAcc) {

        Account account = bankService.getAccountById(idAcc);

        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getLastName(),
                account.getEmail(),
                account.getBalance()
        );
    }

//    @PostMapping
//    public void createAccount(@RequestBody RegisterRequest request) {
//        bankService.registerAccount(request.name(), request.lastName(), request.email(), request.password());
//    }
    @GetMapping("/{idAcc}/balance")
    public BigDecimal getBalance(@PathVariable Integer idAcc) {
        return bankService.getAccountById(idAcc).getBalance();
    }
    @PostMapping("/deposit")
    public void deposit(@Valid @RequestBody DepositRequest depositRequest) {
        bankService.deposit(depositRequest.idAcc(), depositRequest.amount());
    }
    @PostMapping("/withdraw")
    public void withdraw(@Valid @RequestBody WithdrawRequest withdrawRequest) {
        bankService.withdraw(withdrawRequest.idAcc(),withdrawRequest.amount());
    }
    @PostMapping("/transfer")
    public void transfer(@Valid @RequestBody TransferRequest transferRequest) {
        bankService.transfer(transferRequest.senderId(),transferRequest.receiverId(),transferRequest.amount());
    }
    @PatchMapping("/{idAcc}/password")
    public void changePassword(@PathVariable Integer idAcc, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        bankService.changePassword( idAcc, changePasswordRequest.oldPassword(),changePasswordRequest.newPassword());
    }
}
