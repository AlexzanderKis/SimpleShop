package org.com.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.com.domain.dto.CartDTO;
import org.com.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
        REST-контроллер корзины
        Обслуживает JSON-запросы к /api/v1/cart/**
        Позволяет просматривать корзину, добавлять/удалять товары, менять количество, очищать корзину
        Для HTML-страницы корзины смотреть WebCartController
 */
@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Корзина покупателя", description = "Операции с текущей продуктовой корзиной: просмотр, добавление, изменение количества, удаление, очистка")
public class CartRestController {
    private final CartService cartService;

    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    @Operation(summary = "Добавить товар в корзину",
            description = "Добавляет товар указанного ID и количества в корзину (или увеличивает количество, если товар уже добавлен)")
    public ResponseEntity<CartDTO> addItem(@Parameter(description = "ID товара", required = true)
                                           @RequestParam Long productId,
                                           @Parameter(description = "Количество (по умолчанию 1)")
                                           @RequestParam(defaultValue = "1") int quantity){
        return ResponseEntity.ok(cartService.addItem(productId, quantity));
    }

    /**
            Изменяет количество товара в корзине на величину newQuantity
            Если итоговое количество <= 0, позиция удаляется из корзины
            Если товара с указанным productId нет в корзине - выбрасывается исключение (см. CartService)

            @param productId ID товара
            @param newQuantity     изменение количества (может быть отрицательным)
            @return актуальное состояние корзины после изменения
     */
    @PostMapping("/items/{productId}")
    @Operation(summary = "Изменить количество товара в корзине",
            description = "Увеличивает или уменьшает количество единиц товара на величину delta (например, +1 или -1). Если результат <= 0, позиция удаляется.")
    public ResponseEntity<CartDTO> updateQuantity(@PathVariable Long productId,
                                                  @Parameter(description = "Изменение количества (например: 1, -1, 5)")
                                                  @RequestParam int newQuantity){
        return ResponseEntity.ok(cartService.updateQuantity(productId, newQuantity));
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Удалить товар из корзины", description = "Полностью удаляет позицию товара из корзины")
    public ResponseEntity<CartDTO> removeItem(@PathVariable Long productId){
        return ResponseEntity.ok(cartService.removeItem(productId));
    }

    @DeleteMapping
    @Operation(summary = "Очистить корзину", description = "Удаляет все товары из корзины")
    public ResponseEntity<Void> clearCart(){
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}
