package org.com.service;

import org.com.CategoryOfProd;
import org.com.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();

    List<Product> filterByCategory(CategoryOfProd category);

    List<Product> filterByPriceRange(BigDecimal min, BigDecimal max);

    List<Product> searchByKeyword(String keyword);

    Optional<Product> getProductById(Long id);
}