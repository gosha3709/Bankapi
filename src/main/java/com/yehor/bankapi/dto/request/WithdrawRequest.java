package com.yehor.bankapi.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WithdrawRequest(
        @NotNull
        @Positive
        Integer idAcc,

        @NotNull
        @Positive
        @Digits(integer = 10, fraction = 2)
        BigDecimal amount
) {
}
