package com.fincart.expense.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateExpenseRequest {
    @NotBlank
    private String title;
    @NotNull
    @Positive
    private Double amount;
    @NotBlank
    private String category;
    private String description;

    public @NotBlank String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank String title) {
        this.title = title;
    }

    public @NotNull @Positive Double getAmount() {
        return amount;
    }

    public void setAmount(@NotNull @Positive Double amount) {
        this.amount = amount;
    }

    public @NotBlank String getCategory() {
        return category;
    }

    public void setCategory(@NotBlank String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
