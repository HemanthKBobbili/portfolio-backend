package com.hkb.portfolio_backend.dto;

import com.hkb.portfolio_backend.entity.Task;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class TaskDto {
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Priority is required")
    private Task.Priority priority;

    private LocalDateTime dueDate;

    private Boolean completed = false;

    private Long userId;  // For creation (from token)
}
