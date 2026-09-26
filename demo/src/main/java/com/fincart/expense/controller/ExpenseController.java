package com.fincart.expense.controller;

import com.fincart.common.exception.BadRequestException;
import com.fincart.expense.dto.CreateExpenseRequest;
import com.fincart.expense.dto.ExpenseResponse;
import com.fincart.expense.dto.UpdateExpenseRequest;
import com.fincart.expense.service.ExpenseService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private static final Set<String> ALLOWED_SORTS =
            Set.of("expenseDate", "amount", "id");

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> create(
            @Valid @RequestBody CreateExpenseRequest request,
            Authentication authentication) {

        ExpenseResponse created =
                service.create(request, authentication.getName());

        return ResponseEntity
                .created(URI.create("/api/expenses/" + created.id()))
                .body(created);
    }

    @GetMapping("/{id}")
    public ExpenseResponse getOne(
            @PathVariable Long id,
            Authentication authentication) {

        return service.getOne(id, authentication.getName());
    }

    @GetMapping
    public Page<ExpenseResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "expenseDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Authentication authentication) {

        Pageable pageable =
                buildPageable(page, size, sortBy, direction);

        return service.list(
                authentication.getName(),
                category,
                pageable
        );
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateExpenseRequest request,
            Authentication authentication) {

        return service.update(
                id,
                request,
                authentication.getName()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {

        service.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    private Pageable buildPageable(
            int page,
            int size,
            String sortBy,
            String direction) {

        if (page < 0 || size < 1 || size > 100) {
            throw new BadRequestException(
                    "page must be >= 0; size must be between 1 and 100"
            );
        }

        if (!ALLOWED_SORTS.contains(sortBy)) {
            throw new BadRequestException(
                    "sortBy must be expenseDate, amount, or id"
            );
        }

        Sort.Direction sortDirection;
        if ("asc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.ASC;
        } else if ("desc".equalsIgnoreCase(direction)) {
            sortDirection = Sort.Direction.DESC;
        } else {
            throw new BadRequestException(
                    "direction must be asc or desc"
            );
        }

        Sort sort = Sort.by(sortDirection, sortBy);

        if (!"id".equals(sortBy)) {
            sort = sort.and(Sort.by(sortDirection, "id"));
        }

        return PageRequest.of(page, size, sort);
    }
}