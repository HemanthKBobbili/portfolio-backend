package com.hkb.portfolio_backend.repository;

import com.hkb.portfolio_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUserId(Long userId);
    @Query("SELECT p FROM Product p WHERE p.user.id = :userId AND p.stockQuantity < 10")
    List<Product> findLowStockByUser(@Param("userId") Long userId);
}
