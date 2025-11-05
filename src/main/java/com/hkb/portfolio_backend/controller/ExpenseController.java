package com.hkb.portfolio_backend.controller;

import com.hkb.portfolio_backend.dto.ExpenseDto;
import com.hkb.portfolio_backend.repository.UserRepository;
import com.hkb.portfolio_backend.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/finance")
@CrossOrigin(origins = "http://localhost:4200")
public class ExpenseController {

    @Autowired private ExpenseService expenseService;
    @Autowired private UserRepository userRepository;

    @GetMapping @PreAuthorize("isAuthenticated()")
    public List<ExpenseDto> getExpenses(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        return expenseService.getExpensesForUser(userId);
    }

    @PostMapping @PreAuthorize("isAuthenticated()")
    public ExpenseDto createExpense(@Valid @RequestBody ExpenseDto dto, Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        return expenseService.createExpense(dto, userId);
    }

    @PutMapping("/{id}") @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExpenseDto> updateExpense(@PathVariable Long id, @Valid @RequestBody ExpenseDto dto, Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        return expenseService.updateExpense(id, dto, userId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id, Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        expenseService.deleteExpense(id, userId);
        return ResponseEntity.noContent().build();
    }

    private Long getUserIdFromAuth(Authentication auth) {
        org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        String username = userDetails.getUsername();
        return userRepository.findByUsername(username)  // Use injected repo
                .map(com.hkb.portfolio_backend.entity.User::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/import")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> importExpenses(@RequestParam("file") MultipartFile file, Authentication auth) throws Exception {
        Long userId = getUserIdFromAuth(auth);
        String result = expenseService.importExpensesFromCsv(file, userId);
        return ResponseEntity.ok(result);
    }

}