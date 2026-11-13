package org.com.service;

import org.com.domain.dto.ProductDTO;
import org.com.domain.dto.ProductFilterDTO;
import org.com.domain.entity.enums.CategoryOfProd;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
    Сервис товаров
    Инкапсулирует логику фильтрации и поиска по каталогу
    Возвращает DTO, не раскрывает Entity
 */
public interface ProductService {
    Optional<ProductDTO> getProductById(Long id); // получить продукт по ID

    List<ProductDTO> getAllProducts(); // Возвращает все товары каталога

    List<CategoryOfProd> getAllCategories(); // Возвращает список всех категорий

    List<ProductDTO> filterByCategory(CategoryOfProd category); // отфильтровать продукты по категории

    List<ProductDTO> filterByPriceRange(BigDecimal min, BigDecimal max); // фильтровать по цене продукта от мин к мах

    List<ProductDTO> searchByKeyword(String keyword); // искать прод по ключу
    // Универсальная фильтрация по нескольким критериям

    List<ProductDTO> filter(ProductFilterDTO filterDTO);

    // Создаёт новый товар (ADMIN ONLY)
    ProductDTO createProduct(ProductDTO productDTO);

    // Удаляет товар по ID (ADMIN ONLY)
    void deleteProduct(Long id);
}