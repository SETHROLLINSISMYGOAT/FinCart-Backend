package com.fincart;


import com.fincart.expense.dto.CreateExpenseRequest;
import com.fincart.expense.dto.ExpenseResponse;
import com.fincart.expense.entity.Expense;
import com.fincart.expense.repository.ExpenseRepository;
import com.fincart.expense.service.ExpenseService;
import com.fincart.user.entity.User;
import com.fincart.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;


    // --------------------------------------------------
    // 1. CREATE EXPENSE - SUCCESS
    // --------------------------------------------------

    @Test
    void createExpense_success() {

        // Arrange
        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        CreateExpenseRequest request =
                new CreateExpenseRequest();

        request.setTitle("Food");
        request.setAmount(250.0);
        request.setCategory("Food");
        request.setDescription("Lunch");

        Expense savedExpense = new Expense();

        savedExpense.setId(10L);
        savedExpense.setTitle("Food");
        savedExpense.setAmount(250.0);
        savedExpense.setCategory("Food");
        savedExpense.setDescription("Lunch");
        savedExpense.setExpenseDate(LocalDate.now());
        savedExpense.setUser(user);

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(expenseRepository.save(any(Expense.class)))
                .thenReturn(savedExpense);


        // Act
        ExpenseResponse response =
                expenseService.createExpense(
                        request,
                        "test@gmail.com"
                );


        // Assert
        assertNotNull(response);

        assertEquals(10L, response.getId());
        assertEquals("Food", response.getTitle());
        assertEquals(250.0, response.getAmount());
        assertEquals("Food", response.getCategory());

        verify(userRepository)
                .findByEmail("test@gmail.com");

        verify(expenseRepository)
                .save(any(Expense.class));
    }


    // --------------------------------------------------
    // 2. CREATE EXPENSE - USER NOT FOUND
    // --------------------------------------------------

    @Test
    void createExpense_userNotFound() {

        CreateExpenseRequest request =
                new CreateExpenseRequest();

        request.setTitle("Food");
        request.setAmount(250.0);
        request.setCategory("Food");

        when(userRepository.findByEmail("wrong@gmail.com"))
                .thenReturn(Optional.empty());


        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> expenseService.createExpense(
                                request,
                                "wrong@gmail.com"
                        )
                );


        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(expenseRepository, never())
                .save(any(Expense.class));
    }


    // --------------------------------------------------
    // 3. GET EXPENSE BY ID - NOT FOUND
    // --------------------------------------------------

    @Test
    void getExpenseById_notFound() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(expenseRepository.findById(999L))
                .thenReturn(Optional.empty());


        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> expenseService.getExpenseById(
                                999L,
                                "test@gmail.com"
                        )
                );


        assertEquals(
                "Expense not found",
                exception.getMessage()
        );
    }


    // --------------------------------------------------
    // 4. GET EXPENSE - ACCESS DENIED
    // --------------------------------------------------

    @Test
    void getExpenseById_accessDenied() {

        // Logged-in user
        User loggedInUser = new User();
        loggedInUser.setId(1L);
        loggedInUser.setEmail("user1@gmail.com");

        // Expense belongs to another user
        User expenseOwner = new User();
        expenseOwner.setId(2L);
        expenseOwner.setEmail("user2@gmail.com");

        Expense expense = new Expense();

        expense.setId(10L);
        expense.setTitle("Food");
        expense.setAmount(250.0);
        expense.setCategory("Food");
        expense.setExpenseDate(LocalDate.now());
        expense.setUser(expenseOwner);


        when(userRepository.findByEmail("user1@gmail.com"))
                .thenReturn(Optional.of(loggedInUser));

        when(expenseRepository.findById(10L))
                .thenReturn(Optional.of(expense));


        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> expenseService.getExpenseById(
                                10L,
                                "user1@gmail.com"
                        )
                );


        assertEquals(
                "Access denied",
                exception.getMessage()
        );
    }


    // --------------------------------------------------
    // 5. DELETE EXPENSE - SUCCESS
    // --------------------------------------------------

    @Test
    void deleteExpense_success() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@gmail.com");

        Expense expense = new Expense();

        expense.setId(10L);
        expense.setTitle("Food");
        expense.setAmount(250.0);
        expense.setCategory("Food");
        expense.setExpenseDate(LocalDate.now());
        expense.setUser(user);


        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(user));

        when(expenseRepository.findById(10L))
                .thenReturn(Optional.of(expense));


        expenseService.deleteExpense(
                10L,
                "test@gmail.com"
        );


        verify(expenseRepository)
                .delete(expense);
    }


    // --------------------------------------------------
    // 6. DELETE EXPENSE - ACCESS DENIED
    // --------------------------------------------------

    @Test
    void deleteExpense_accessDenied() {

        User loggedInUser = new User();
        loggedInUser.setId(1L);
        loggedInUser.setEmail("user1@gmail.com");

        User expenseOwner = new User();
        expenseOwner.setId(2L);
        expenseOwner.setEmail("user2@gmail.com");

        Expense expense = new Expense();

        expense.setId(10L);
        expense.setUser(expenseOwner);


        when(userRepository.findByEmail("user1@gmail.com"))
                .thenReturn(Optional.of(loggedInUser));

        when(expenseRepository.findById(10L))
                .thenReturn(Optional.of(expense));


        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> expenseService.deleteExpense(
                                10L,
                                "user1@gmail.com"
                        )
                );


        assertEquals(
                "Access denied",
                exception.getMessage()
        );

        verify(expenseRepository, never())
                .delete(any(Expense.class));
    }
}