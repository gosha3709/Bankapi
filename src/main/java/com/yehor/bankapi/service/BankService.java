package com.yehor.bankapi.service;
import java.util.List;
import com.yehor.bankapi.exception.AccountNotFoundException;
import com.yehor.bankapi.model.Account;
import com.yehor.bankapi.model.Transaction;
import com.yehor.bankapi.model.TransactionType;
import com.yehor.bankapi.model.Transfer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yehor.bankapi.repository.AccountRepository;
import com.yehor.bankapi.repository.TransactionRepository;
import com.yehor.bankapi.repository.TransferRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Service
public class BankService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransferRepository transferRepository;
    private final PasswordEncoder passwordEncoder;

    public BankService(AccountRepository accountRepository, TransactionRepository transactionRepository, TransferRepository transferRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.transferRepository = transferRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));
    }
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionByType(Integer accountId, TransactionType transactionType){
        if (accountId == null || transactionType == null) {
            throw new IllegalArgumentException("Account ID and transaction type cannot be null");
        }
        getAccountById(accountId);
        return transactionRepository.findByAccount_IdAndTransactionType(accountId, transactionType);
    }
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsSortedByAmount(Integer accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }
        getAccountById(accountId);
        return transactionRepository.findByAccount_IdOrderByAmountAsc(accountId);
    }
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAccountId(Integer accountId) {

        getAccountById(accountId);

        return transactionRepository.findByAccount_Id(accountId);
    }

    @Transactional
    public void deposit(Integer accountId, BigDecimal amount) {

        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        account.deposit(amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                account,
                null,
                amount,
                TransactionType.DEPOSIT
        );

        transactionRepository.save(transaction);
    }

    @Transactional
    public void withdraw(Integer accountId, BigDecimal amount) {

        Account account = accountRepository.findByIdForUpdate(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        account.withdraw(amount);
        accountRepository.save(account);

        Transaction transaction = new Transaction(
                account,
                null,
                amount,
                TransactionType.WITHDRAW
        );

        transactionRepository.save(transaction);
    }
    @Transactional
    public void registerAccount(String name,String lastname,String email,String password){
        if (accountRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        String passwordHash = passwordEncoder.encode(password);
        Account account = new Account(
                name,
                lastname,
                email,
                passwordHash
        );
        accountRepository.save(account);

    }

    @Transactional
    public void transfer(Integer sender,Integer receiver,BigDecimal amount){

        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }

        if (sender.equals(receiver)) {
            throw new IllegalArgumentException("Cannot transfer to yourself");
        }
        Integer firstId = Math.min(sender, receiver);
        Integer secondId = Math.max(sender, receiver);

        Account firstAccount = accountRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Account secondAccount = accountRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Account account1 = sender.equals(firstId) ? firstAccount : secondAccount;
        Account account2 = receiver.equals(firstId) ? firstAccount : secondAccount;
        account1.withdraw(amount);
        account2.deposit(amount);

        accountRepository.save(account1);
        accountRepository.save(account2);

        Transfer transfer = new Transfer(
                account1,
                account2,
                amount
        );
        transferRepository.save(transfer);
        Transaction transaction1 = new Transaction(
                  account1,
                  transfer,
                  amount,
                  TransactionType.TRANSFER_OUT
        );
          transactionRepository.save(transaction1);

        Transaction transaction2 = new Transaction(
                account2,
                transfer,
                amount,
                TransactionType.TRANSFER_IN
        );
          transactionRepository.save(transaction2);
    }
    @Transactional(readOnly = true)
    public boolean login(String email, String password) {
        if (email == null || password == null) {
            return false;
        }
        Account account = accountRepository.findByEmail(email)
                .orElse(null);

        if (account == null) {
            return false;
        }
            return passwordEncoder.matches(password, account.getPasswordHash());
    }

    @Transactional
    public void changePassword(Integer acc_id, String oldPassword, String newPassword){

            if (oldPassword == null || newPassword == null) {
                throw new IllegalArgumentException("Passwords cannot be null");
            }
            Account account = getAccountById(acc_id);
            if (!passwordEncoder.matches(oldPassword, account.getPasswordHash())) {
                throw new IllegalArgumentException("Incorrect old password");
            }

            String newPasswordHash = passwordEncoder.encode(newPassword);
            account.changePasswordHash(newPasswordHash);
            accountRepository.save(account);
    }


}