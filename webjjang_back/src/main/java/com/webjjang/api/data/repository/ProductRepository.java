package com.webjjang.api.data.repository;

import com.webjjang.api.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

// Repository 역할하는 CRUD 처리 -> JpaRepository<대상엔티티, id 타입>
public interface ProductRepository
        extends JpaRepository<Product, Long> {
}
