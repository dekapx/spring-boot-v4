package com.dekapx.apps.orderagent.repository;

import com.dekapx.apps.orderagent.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySkuIgnoreCase(String sku);

    List<Product> findByIdIn(List<Long> ids);
}
