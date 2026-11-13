package org.com.service.impl;

import org.com.domain.dto.ProductDTO;
import org.com.domain.dto.ProductFilterDTO;
import org.com.domain.entity.ProductEntity;
import org.com.domain.entity.enums.CategoryOfProd;
import org.com.exceptions.ResourceNotFoundException;
import org.com.repos.ProductRepo;
import org.com.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;

    public ProductServiceImpl(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    // Получение товара по ID
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDTO> getProductById(Long id) {
        return productRepo.findById(id).map(this::mapToDTO);
    }

    // Возвращает все товары каталога
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepo.findAll().stream().map(this::mapToDTO).toList();
    }

    // Возвращает все категории продуктов
    @Override
    public List<CategoryOfProd> getAllCategories() {
        return List.of();
    }

    // Фильтрация по категории
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> filterByCategory(CategoryOfProd category) {
        return productRepo.findByCategory(category).stream().map(this::mapToDTO).toList();
    }

    // Фильтрация по цене от мин к мах
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> filterByPriceRange(BigDecimal min, BigDecimal max) {
        if (min == null || max == null) {
            throw new NullPointerException("Диапазон не должен быть пустым");
        }

        // BigDecimal moreThan & lessThan compare
        if (min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(min) < 0) {
            throw new IllegalArgumentException("Некорректный диапазон цен. Минимальная цена не может быть отрицательной или больше максимальной.");
        }

        return productRepo.findByPriceBetweenOrderByPrice(min, max).stream().map(this::mapToDTO).toList();
//        return productRepo.getAllProduct().stream()
//                .filter(product -> product.getProductPrice().compareTo(min) >= 0
//                        && product.getProductPrice().compareTo(max) <= 0)
//                .sorted(Comparator.comparing(Product::getProductPrice))
//                .collect(Collectors.toList());
    }

    // Фильтрация по ключевому слову
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productRepo.searchByKeyword(keyword.trim().toLowerCase(Locale.ROOT)).stream()
                .map(this::mapToDTO).toList();
    }

    // Фильтрация по категории, мин/мах цене, ключ словам
    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> filter(ProductFilterDTO filterDTO) {
        if (filterDTO == null) {
            return getAllProducts();
        }

        // null, чтобы условие :keyword IS NULL в JPQL сработало
        String keyword = (filterDTO.getKeywords() != null && !filterDTO.getKeywords().trim().isEmpty()) ? filterDTO.getKeywords().trim() : null;
        return productRepo.filterProducts(
                filterDTO.getCategory(),
                filterDTO.getMinPrice(),
                filterDTO.getMaxPrice(),
                keyword).stream().map(this::mapToDTO).toList();
    }

    // Создание нового продукта (ADMIN ONLY)
    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        ProductEntity product = new ProductEntity(
                productDTO.getId(),
                productDTO.getName(),
                productDTO.getBrand(),
                productDTO.getCategory(),
                productDTO.getPrice(),
                productDTO.getKeywords(),
                productDTO.getImageUrl());

        ProductEntity savedProd = productRepo.save(product);
        return mapToDTO(savedProd);
    }

    // Удаление продукта (ADMIN ONLY)
    @Override
    public void deleteProduct(Long id) {
        if (!productRepo.existsById(id)){ // existsById эффективнее - не загружает сущность
            throw new ResourceNotFoundException("Товар с ID "+id+" не найден");
        }
        productRepo.deleteById(id);
    }

    // маппинг Entity в DTO
    private ProductDTO mapToDTO(ProductEntity productEntity) {
        return new ProductDTO(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getCategory(),
                productEntity.getBrand(),
                productEntity.getPrice(),
                productEntity.getKeywords(),
                productEntity.getImageUrl());
    }
}