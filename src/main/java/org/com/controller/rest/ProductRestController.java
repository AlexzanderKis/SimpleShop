package org.com.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.com.domain.dto.ProductDTO;
import org.com.domain.dto.ProductFilterDTO;
import org.com.domain.entity.enums.CategoryOfProd;
import org.com.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
        REST-контроллер товаров
        Обслуживает JSON-запросы к /api/v1/products/**
        Позволяет получать список товаров с фильтрацией, список категорий, товар по ID
        а также создавать и удалять товары (ADMIN)
        Для HTML-страниц каталога см. WebCatalogController
 */
// список товаров с фильтрацией (keyword, category, minPrice, maxPrice), список категорий, товар по ID, создание (админ), удаление (админ)
// возвращает JSON
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Товары и Каталог", description = "Управление товарами: просмотр, фильтрация по категориям, ценам, поиск по ключевым словам")
public class ProductRestController {

    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Получить список всех товаров или отфильтровать", description = "Возвращает доступные товары с возможностью фильтрации по ключевым словам, категориям и диапазону цен")
    public ResponseEntity<List<ProductDTO>> getProduct(@Parameter(description = "Ключевое слово для поиска (по названию, бренду или тегам)")
                                                       @RequestParam(required = false) String keyword,
                                                       @Parameter(description = "Категория товара")
                                                       @RequestParam(required = false)CategoryOfProd category,
                                                       @Parameter(description = "Минимальная цена")
                                                       @RequestParam(required = false)BigDecimal minPrice,
                                                       @Parameter(description = "Максимальная цена")
                                                       @RequestParam(required = false)BigDecimal maxPrice){
        if (keyword != null || category != null || minPrice != null || maxPrice != null){
            ProductFilterDTO filterDTO = new ProductFilterDTO(keyword,category,minPrice,maxPrice);
            return ResponseEntity.ok(productService.filter(filterDTO));
        }
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/categories")
    @Operation(summary = "Список всех категорий товаров", description = "Возвращает перечень доступных категорий каталога (FRUIT, VEGETABLE, MEAT и т.д.)")
    public ResponseEntity<List<CategoryOfProd>> getCategories(){
        return ResponseEntity.ok(productService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар по ID", description = "Возвращает подробные сведения о товаре")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Товар найден"),
            @ApiResponse(responseCode = "404", description = "Товар не найден")})
    public ResponseEntity<ProductDTO> getProductBtId(@PathVariable Long id){
        return productService.getProductById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Добавить новый товар (ADMIN ONLY)", description = "Создание новой позиции в каталоге товаров магазина")
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO){
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить товар (ADMIN ONLY)", description = "Удаление товара из базы данных по его ID")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
