package org.com.repos;

import org.com.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepo {

    List<Product> getAllProduct(); // список продуктов

    Optional<Product> findById(Long id);

    //    Optional<Product> findBySimplifiedName(String name);

    void save(Product product);

    void deleteById(Long id);
}