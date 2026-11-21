package com.yehor.bankapi.model;

import com.yehor.bankapi.exception.InsufficientFundsException;
import com.yehor.bankapi.exception.InvalidAmountException;
import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acc_id")
    private Integer id;

    @Column(name = "balance", nullable = false, precision = 12, scale = 2)
    private BigDecimal balance;
    @Column(name = "name", nullable = false, length = 15)
    private String name;
    @Column(name = "lastname", nullable = false,  length = 25)
    private String lastName;
    @Column(name = "email",nullable = false, unique = true, length = 50)
    private String email;
    @Column(name = "password_hash", nullable = false,  length = 255)
    private String passwordHash;

    public Account(String name, String lastName, String email, String passwordHash) {
        this.balance = BigDecimal.ZERO;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    protected Account() {
    }

    public Integer getId() {
        return id;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public String getName() {
        return name;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getPasswordHash() {
        return passwordHash;
    }

    public void deposit(BigDecimal amount){
        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountException(amount);
        }
        balance = balance.add(amount);
    }
    public void withdraw(BigDecimal amount){

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(amount);
        }

        if (amount.compareTo(balance) > 0) {
            throw new InsufficientFundsException();
        }

        balance = balance.subtract(amount);
    }

    public void changePasswordHash(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

}
