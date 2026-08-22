package com.fincart.expense.service;

import com.fincart.expense.dto.CreateExpenseRequest;
import com.fincart.expense.dto.ExpenseResponse;
import com.fincart.expense.dto.UpdateExpenseRequest;
import com.fincart.expense.entity.Expense;
import com.fincart.expense.repository.ExpenseRepository;
import com.fincart.user.entity.User;
import com.fincart.user.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository) {

        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    public ExpenseResponse createExpense(
            CreateExpenseRequest request,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Expense expense = new Expense();

        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(LocalDate.now());
        expense.setUser(user);

        Expense saved = expenseRepository.save(expense);

        return mapToResponse(saved);
    }

    public Page<ExpenseResponse> getExpenses(
            String email,
            String category,
            Pageable pageable) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Page<Expense> expenses;

        if (category != null && !category.isBlank()) {

            expenses = expenseRepository
                    .findByUserIdAndCategory(
                            user.getId(),
                            category,
                            pageable);

        } else {

            expenses = expenseRepository
                    .findByUserId(
                            user.getId(),
                            pageable);
        }

        return expenses.map(this::mapToResponse);
    }

    public ExpenseResponse getExpenseById(
            Long id,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Expense expense = expenseRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return mapToResponse(expense);
    }

    public void deleteExpense(
            Long id,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Expense expense = expenseRepository
                .findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        expenseRepository.delete(expense);
    }
    public ExpenseResponse updateExpense(Long id, UpdateExpenseRequest request, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(()-> new RuntimeException("User not found"));
        Expense expense = expenseRepository.findById(id).orElseThrow(()-> new RuntimeException("Expense not found"));
        if(!expense.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }
        expense.setTitle(request.getTitle());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());

        Expense updated =
                expenseRepository.save(expense);

        return mapToResponse(updated);


    }

    private ExpenseResponse mapToResponse(Expense expense) {

        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getExpenseDate()
        );
    }

}