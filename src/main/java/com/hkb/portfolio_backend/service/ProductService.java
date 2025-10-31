package com.hkb.portfolio_backend.service;

import com.hkb.portfolio_backend.dto.ProductDto;
import com.hkb.portfolio_backend.entity.Product;
import com.hkb.portfolio_backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired private ProductRepository productRepository;
    private final Path uploadDir = Paths.get("uploads");  // Local folder for images

    public ProductService() throws IOException {
        Files.createDirectories(uploadDir);  // Create uploads folder
    }

    // Get all products for user
    public List<ProductDto> getProductsForUser(Long userId) {
        return productRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Create product with file upload
    @Transactional
    public ProductDto createProduct(ProductDto dto, MultipartFile file, Long userId) throws IOException {
        Product product = convertToEntity(dto);
        if (file != null && !file.isEmpty()) {
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            product.setImagePath(filePath.toString());
        }
        product.setUser(new com.hkb.portfolio_backend.entity.User());
        product.getUser().setId(userId);
        product = productRepository.save(product);
        return convertToDto(product);
    }

    // Update product
    @Transactional
    public Optional<ProductDto> updateProduct(Long id, ProductDto dto, Long userId) {
        return productRepository.findById(id)
                .filter(p -> p.getUser().getId().equals(userId))
                .map(p -> {
                    p.setName(dto.getName());
                    p.setPrice(dto.getPrice());
                    p.setStockQuantity(dto.getStockQuantity());
                    return convertToDto(productRepository.save(p));
                });
    }

    // Delete product
    @Transactional
    public void deleteProduct(Long id, Long userId) {
        productRepository.findById(id)
                .filter(p -> p.getUser().getId().equals(userId))
                .ifPresent(productRepository::delete);
    }

    // Generate low-stock report (using Streams)
    public List<ProductDto> getLowStockReport(Long userId) {
        return productRepository.findLowStockByUser(userId).stream()
                .filter(p -> p.getStockQuantity() > 0)  // Exclude out-of-stock
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helpers
    private ProductDto convertToDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setImagePath(product.getImagePath());
        dto.setUserId(product.getUser().getId());
        return dto;
    }

    private Product convertToEntity(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        return product;
    }
}
