package com.fincart.expense.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        String title,
        BigDecimal amount,
        String category,
        String description,
        LocalDate expenseDate,
        Instant createdAt,
        Long version
) {}