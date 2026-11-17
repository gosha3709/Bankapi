package com.yehor.bankapi.dto.response;

import com.yehor.bankapi.model.TransactionType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionResponse(
        Integer id,
        Integer accountId,
        BigDecimal amount,
        TransactionType type,
        OffsetDateTime createdAt
) {
}