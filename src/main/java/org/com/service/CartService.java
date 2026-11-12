package org.com.service;

import org.com.domain.dto.CartDTO;
import org.com.domain.dto.CartItemDTO;

import java.math.BigDecimal;
import java.util.List;

// описывает операции с корзиной
/**
   Сервис корзины покупателя
   получение, добавление товара, изменение количества, удаление позиции, очистка

   Все методы возвращают актуальное состояние корзины в виде CartDto, кроме clearCart() и getTotalPrice() getItems()
 */
public interface CartService {

    // Возвращает текущее состояние корзины
    // DTO с позициями, итоговой суммой и общим количеством
    CartDTO getCart();

    // Добавляет товар в корзину или увеличивает количество, если товар уже добавлен
    CartDTO addItem(Long productId, int quantity);

    CartDTO removeItem(Long productId);

    CartDTO updateQuantity(Long productId, int quantity);

    // Возвращает общую стоимость корзины
    BigDecimal getTotalPrice();

    // Возвращает копию списка позиций корзины
    List<CartItemDTO> getItems();

    void clearCart();
}