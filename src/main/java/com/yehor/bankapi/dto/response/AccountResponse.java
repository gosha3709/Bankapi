package com.yehor.bankapi.dto.response;

import java.math.BigDecimal;

public record AccountResponse(
        Integer id,
        String name,
        String lastName,
        String email,
        BigDecimal balance
) {
}
