package org.com;

import org.com.repos.ProductRepo;
import org.com.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;

    public ProductServiceImpl(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public ProductRepo getProductRepo() {
        return productRepo;
    }

    @Override
    public List<Product> getAllProduct() {
        return List.copyOf(productRepo.getAllProduct());
    }

    @Override
    public List<CategoryOfProd> getAllProductCategories() {
        return List.of(CategoryOfProd.values());
    }

    @Override
    public List<Product> filterProductByCategory(CategoryOfProd category) {
        return productRepo.getAllProduct().stream().filter(product -> product.getProductCategory() == category).toList();
    }


    @Override
    public List<Product> filterProductByPriceRange(BigDecimal min, BigDecimal max) {
        // BigDecimal moreThan & lessThat compare
        return productRepo.getAllProduct().stream()
                .filter(product -> product.getProductPrice().compareTo(min) >= 0
                        && product.getProductPrice().compareTo(max) <= 0)
                .toList();
    }

    @Override
    public List<Product> searchProductByKeyword(String keyword) {
        return productRepo.getAllProduct().stream()
                .filter(product -> product.getProductKeywords()
                        .stream().anyMatch(string -> string.contains(keyword.toLowerCase())))
                .toList();
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return Optional.ofNullable(productRepo.findById(id));
    }
}