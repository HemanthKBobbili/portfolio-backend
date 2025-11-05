package com.hkb.portfolio_backend.service;


import com.hkb.portfolio_backend.dto.ExpenseDto;
import com.hkb.portfolio_backend.entity.Expense;
import com.hkb.portfolio_backend.repository.ExpenseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private static final Logger log = LoggerFactory.getLogger(ExpenseService.class);

    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private JobLauncher jobLauncher;
    @Autowired private Job importExpensesJob;

    private final Path uploadDir = Paths.get("uploads", "finance").toAbsolutePath().normalize();
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final Set<String> ALLOWED_TYPES = Set.of("text/csv", "application/vnd.ms-excel", "text/plain");

    public ExpenseService() throws IOException {
        Files.createDirectories(uploadDir);
    }

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

    public String importExpensesFromCsv(MultipartFile file, Long userId) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is required");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("CSV file too large (max 2MB)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid CSV content type: " + contentType);
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        original = original.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        String storedName = UUID.randomUUID().toString() + "_" + original;
        Path filePath = uploadDir.resolve(storedName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        log.info("Saved expense CSV to {}", filePath);

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("filePath", filePath.toString())
                .addLong("userId", userId)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobLauncher.run(importExpensesJob, jobParameters);
        return "Import " + execution.getStatus() + " for file " + storedName;
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
}