package com.hkb.portfolio_backend.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data  // Lombok: Generates getters, setters, toString, equals, hashCode
public class ProductDto {
    private Long id;  // Auto-generated on creation

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;  // Optional

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private String imagePath;  // Set after file upload

    private Long userId;  // For associating with the authenticated user (from token)
}
