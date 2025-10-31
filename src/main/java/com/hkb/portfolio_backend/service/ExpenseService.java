package com.hkb.portfolio_backend.service;

import com.hkb.portfolio_backend.dto.ExpenseDto;
import com.hkb.portfolio_backend.entity.Expense;
import com.hkb.portfolio_backend.repository.ExpenseRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired private ExpenseRepository expenseRepository;

    @Autowired private JobLauncher jobLauncher;
    @Autowired private Job importExpensesJob;

    public List<ExpenseDto> getExpensesForUser(Long userId) {
        return expenseRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExpenseDto createExpense(ExpenseDto dto, Long userId) {
        Expense expense = convertToEntity(dto);
        expense.setUser(new com.hkb.portfolio_backend.entity.User());
        expense.getUser().setId(userId);
        expense = expenseRepository.save(expense);
        return convertToDto(expense);
    }

    @Transactional
    public Optional<ExpenseDto> updateExpense(Long id, ExpenseDto dto, Long userId) {
        return expenseRepository.findById(id)
                .filter(e -> e.getUser().getId().equals(userId))
                .map(e -> {
                    e.setDescription(dto.getDescription());
                    e.setAmount(dto.getAmount());
                    e.setDate(dto.getDate());
                    e.setCategory(dto.getCategory());
                    return convertToDto(expenseRepository.save(e));
                });
    }

    @Transactional
    public void deleteExpense(Long id, Long userId) {
        expenseRepository.findById(id)
                .filter(e -> e.getUser().getId().equals(userId))
                .ifPresent(expenseRepository::delete);
    }

    private ExpenseDto convertToDto(Expense expense) {
        ExpenseDto dto = new ExpenseDto();
        dto.setId(expense.getId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        dto.setCategory(expense.getCategory());
        dto.setUserId(expense.getUser().getId());
        return dto;
    }

    private Expense convertToEntity(ExpenseDto dto) {
        Expense expense = new Expense();
        expense.setDescription(dto.getDescription());
        expense.setAmount(dto.getAmount());
        expense.setDate(dto.getDate());
        expense.setCategory(dto.getCategory());
        return expense;
    }

    public String importExpensesFromCsv(MultipartFile file, Long userId) throws Exception {
        String filename = "expenses.csv";
        Path filePath = Paths.get("uploads", filename);
        Files.createDirectories(filePath.getParent());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", filePath.toString())
                .addLong("userId", userId)
                .addLong("timestamp", System.currentTimeMillis())  // Unique parameter
                .toJobParameters();
        JobExecution execution = jobLauncher.run(importExpensesJob, jobParameters);
        return "Import " + execution.getStatus() + " for " + filename;
    }
}