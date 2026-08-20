package org.com.service;

import org.com.CategoryOfProd;
import org.com.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProduct(); // список продуктов

    List<CategoryOfProd> getAllProductCategories(); // список категорий

    List<Product> filterProductByCategory(CategoryOfProd category); // отфильтровать продукты по категории

    List<Product> filterProductByPriceRange(BigDecimal min, BigDecimal max); // фильтровать по цене продукта от мин к мах

    List<Product> searchProductByKeyword(String keyword); // искать прод по ключу

    Optional<Product> getProductById(Long id); // получить продукт по ID
}