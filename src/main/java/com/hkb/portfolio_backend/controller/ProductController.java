package com.hkb.portfolio_backend.controller;

import com.hkb.portfolio_backend.dto.ProductDto;
import com.hkb.portfolio_backend.repository.UserRepository;
import com.hkb.portfolio_backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    @Autowired private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping @PreAuthorize("isAuthenticated()")
    public List<ProductDto> getProducts(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        return productService.getProductsForUser(userId);
    }

    @PostMapping @PreAuthorize("isAuthenticated()")
    public ProductDto createProduct(@Valid @ModelAttribute ProductDto dto, @RequestParam(value = "file", required = false) MultipartFile file, Authentication auth) throws IOException {
        Long userId = getUserIdFromAuth(auth);
        return productService.createProduct(dto, file, userId);
    }

    @PutMapping("/{id}") @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDto dto, Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        return productService.updateProduct(id, dto, userId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        productService.deleteProduct(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reports/low-stock") @PreAuthorize("isAuthenticated()")
    public List<ProductDto> getLowStockReport(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        return productService.getLowStockReport(userId);
    }

    // Helper: Extract userId from JWT token (via username from UserDetails)
    private Long getUserIdFromAuth(Authentication authentication) {
        // Get the UserDetails object from principal
        org.springframework.security.core.userdetails.User userDetails =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        // Extract username from UserDetails
        String username = userDetails.getUsername();

        // Fetch userId from repository
        return userRepository.findByUsername(username)
                .map(com.hkb.portfolio_backend.entity.User::getId)
                .orElseThrow(() -> new RuntimeException("User not found from token: " + username));
    }
}