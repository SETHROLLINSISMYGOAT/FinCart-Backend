package com.fincart.expense.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateExpenseRequest(
        @NotBlank
        @Size(max = 120)
        String title,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal amount,

        @NotBlank
        @Size(max = 40)
        String category,

        @Size(max = 500)
        String description,

        @NotNull
        LocalDate expenseDate,

        @NotNull
        @Min(0)
        Long version
) {}