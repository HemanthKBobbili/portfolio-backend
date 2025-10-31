package com.hkb.portfolio_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class ProjectDto {
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3-200 characters")
    private String title;

    @Size(max = 1000, message = "Description too long")
    private String description;

    private String techStack;  // JSON string

    private String githubUrl;

    private String liveDemoUrl;

    private LocalDateTime createdAt;

    private Long userId;  // For creation (links to user)
}
