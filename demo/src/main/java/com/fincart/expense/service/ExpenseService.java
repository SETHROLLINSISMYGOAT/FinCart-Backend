package com.fincart.expense.service;

import com.fincart.common.exception.BadRequestException;
import com.fincart.common.exception.ConflictException;
import com.fincart.common.exception.ResourceNotFoundException;
import com.fincart.expense.dto.CreateExpenseRequest;
import com.fincart.expense.dto.ExpenseResponse;
import com.fincart.expense.dto.UpdateExpenseRequest;
import com.fincart.expense.entity.Expense;
import com.fincart.expense.repository.ExpenseRepository;
import com.fincart.user.entity.User;
import com.fincart.user.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ExpenseService {

    private final ExpenseRepository expenses;
    private final UserRepository users;

    public ExpenseService(
            ExpenseRepository expenses,
            UserRepository users) {
        this.expenses = expenses;
        this.users = users;
    }

    @Transactional
    public ExpenseResponse create(
            CreateExpenseRequest request,
            String authenticatedEmail) {

        User owner = requireUser(authenticatedEmail);

        Expense expense = new Expense();
        expense.setTitle(request.title().trim());
        expense.setAmount(request.amount());
        expense.setCategory(request.category().trim());
        expense.setDescription(request.description());
        expense.setExpenseDate(request.expenseDate());
        expense.setUser(owner);

        Expense saved = expenses.save(expense);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getOne(
            Long expenseId,
            String authenticatedEmail) {

        User owner = requireUser(authenticatedEmail);

        Expense expense = expenses
                .findByIdAndUserId(expenseId, owner.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense not found")
                );

        return toResponse(expense);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> list(
            String authenticatedEmail,
            String category,
            Pageable pageable) {

        User owner = requireUser(authenticatedEmail);

        Page<Expense> page;

        if (category == null || category.isBlank()) {
            page = expenses.findByUserId(owner.getId(), pageable);
        } else {
            page = expenses.findByUserIdAndCategory(
                    owner.getId(),
                    category.trim(),
                    pageable
            );
        }

        return page.map(this::toResponse);
    }

    @Transactional
    public ExpenseResponse update(
            Long expenseId,
            UpdateExpenseRequest request,
            String authenticatedEmail) {

        User owner = requireUser(authenticatedEmail);

        Expense expense = expenses
                .findByIdAndUserId(expenseId, owner.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense not found")
                );

        if (!Objects.equals(
                expense.getVersion(),
                request.version())) {
            throw new ConflictException(
                    "Expense changed since you loaded it. Refresh and try again."
            );
        }

        expense.setTitle(request.title().trim());
        expense.setAmount(request.amount());
        expense.setCategory(request.category().trim());
        expense.setDescription(request.description());
        expense.setExpenseDate(request.expenseDate());


        try {
            expenses.flush();
        } catch (ObjectOptimisticLockingFailureException ex) {
            throw new ConflictException(
                    "Expense changed since you loaded it. Refresh and try again."
            );
        }

        return toResponse(expense);
    }

    @Transactional
    public void delete(
            Long expenseId,
            String authenticatedEmail) {

        User owner = requireUser(authenticatedEmail);

        Expense expense = expenses
                .findByIdAndUserId(expenseId, owner.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Expense not found")
                );

        expenses.delete(expense);
    }

    private User requireUser(String email) {
        return users.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found"
                        )
                );
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getExpenseDate(),
                expense.getCreatedAt(),
                expense.getVersion()
        );
    }
}