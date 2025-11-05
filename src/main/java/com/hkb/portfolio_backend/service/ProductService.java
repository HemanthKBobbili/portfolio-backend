package com.hkb.portfolio_backend.service;

import com.hkb.portfolio_backend.dto.ProductDto;
import com.hkb.portfolio_backend.entity.Product;
import com.hkb.portfolio_backend.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    private final Path uploadDir = Paths.get("uploads", "products").toAbsolutePath().normalize();

    // Allowed MIME types
    private static final Set<String> ALLOWED_TYPES = Set.of("image/png", "image/jpeg", "image/jpg");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public ProductService() throws IOException {
        Files.createDirectories(uploadDir); // create "uploads/products" if missing
    }

    public List<ProductDto> getProductsForUser(Long userId) {
        return productRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductDto createProduct(ProductDto dto, MultipartFile file, Long userId) throws IOException {
        Product product = convertToEntity(dto);

        if (file != null && !file.isEmpty()) {
            product.setImagePath(storeFileSafely(file));
        }

        product.setUser(new com.hkb.portfolio_backend.entity.User());
        product.getUser().setId(userId);
        product = productRepository.save(product);
        return convertToDto(product);
    }

    @Transactional
    public Optional<ProductDto> updateProduct(Long id, ProductDto dto, Long userId) {
        return productRepository.findById(id)
                .filter(p -> p.getUser().getId().equals(userId))
                .map(p -> {
                    p.setName(dto.getName());
                    p.setPrice(dto.getPrice());
                    p.setStockQuantity(dto.getStockQuantity());
                    p.setDescription(dto.getDescription());
                    return convertToDto(productRepository.save(p));
                });
    }

    @Transactional
    public void deleteProduct(Long id, Long userId) {
        productRepository.findById(id)
                .filter(p -> p.getUser().getId().equals(userId))
                .ifPresent(productRepository::delete);
    }

    public List<ProductDto> getLowStockReport(Long userId) {
        return productRepository.findLowStockByUser(userId).stream()
                .filter(p -> p.getStockQuantity() > 0)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // --- File handling improvements ---
    private String storeFileSafely(MultipartFile file) throws IOException {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File too large (max 5MB)");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Invalid file type. Allowed: JPEG, PNG");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        originalName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

        String storedFileName = UUID.randomUUID().toString() + "_" + originalName;
        Path targetPath = uploadDir.resolve(storedFileName);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        log.info("Stored file safely: {}", targetPath);

        return storedFileName;
    }

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