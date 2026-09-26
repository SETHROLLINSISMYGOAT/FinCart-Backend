package com.fincart.expense.entity;

import com.fincart.user.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "expenses",
        indexes = {
                @Index(
                        name = "idx_expenses_user_date_id",
                        columnList = "user_id, expense_date, id"
                ),
                @Index(
                        name = "idx_expenses_user_category_date",
                        columnList = "user_id, category, expense_date"
                )
        }
)
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 40)
    private String category;

    @Column(length = 500)
    private String description;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public BigDecimal getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public Instant getCreatedAt() { return createdAt; }
    public User getUser() { return user; }
    public Long getVersion() { return version; }

    public void setTitle(String title) { this.title = title; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    public void setUser(User user) { this.user = user; }
}