package com.fincart.expense.dto;

import java.time.LocalDate;

public class ExpenseResponse {
    private Long id;
    private String description;
    private String title;
    private Double amount;
    private String category;
    private LocalDate expenseDate;
    public ExpenseResponse() {}

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExpenseResponse(Long id, String description, String title, Double amount, String category, LocalDate expenseDate) {
        this.id = id;
        this.description = description;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.expenseDate = expenseDate;

    }


}
