package org.com.service;

import org.com.domain.dto.CreateOrderDTO;
import org.com.domain.dto.OrderResponseDTO;
import org.com.domain.dto.OrderStatusUpdateDTO;
import org.com.domain.dto.UpdateOrderAddressDTO;

import java.math.BigDecimal;
import java.util.List;

// Сервис для создания заказа из текущей корзины пользователя
// вся бизнес-логика: создание из корзины, отмена, повтор, смена адреса, смена статуса, расчёт скидки

// Возвращает DTO, не раскрывает Entity наружу
// Все методы, изменяющие заказ, проверяют права пользователя (кроме updateOrderStatus — только ADMIN)
public interface OrderService {
    /**
        Создаёт заказ из текущей корзины пользователя
        1. Загружает корзину; если пуста — исключение
        2. Фиксирует снимок названий, брендов и цен на момент заказа
        3. Рассчитывает базовую сумму и сумму со скидкой
        4. Сохраняет заказ со статусом NEW_ORDER
        5. Очищает корзину
     */
    OrderResponseDTO createOrder(String email, CreateOrderDTO dto); // можно передать корзину

    OrderResponseDTO getOrderById(String orderId);

    List<OrderResponseDTO> getOrderByUserEmail(String userEmail);

    // Возвращает ВСЕ заказы магазина (ADMIN ONLY)
    List<OrderResponseDTO> getAllOrders();

    /**
        Отменяет заказ
        Проверяет, что заказ принадлежит пользователю
        Переводит статус в CANCELED; если заказ уже доставлен или отменён - исключение
        @param orderId ID заказа
        @param userEmail email пользователя (для проверки прав)
        @return DTO с обновлённым статусом
     */
    OrderResponseDTO cancelOrder(String orderId, String userEmail);

    /**
         Повторяет предыдущий заказ
         ВАЖНО: не создаёт заказ сразу, а копирует товары из старого заказа в корзину и создаёт черновик
         Пользователь должен подтвердить оформление
         @param orderId ID старого заказа
         @param userEmail email пользователя (для проверки прав)
         @return DTO черновика нового заказа
     */
    OrderResponseDTO repeatOrder(String orderId, String userEmail);

    OrderResponseDTO updateOrderDeliveryAddress(String orderId, UpdateOrderAddressDTO dto, String userEmail); // изменение заказа (адрес доставки)

    // Обновляет статус заказа (ADMIN ONLY)
    OrderResponseDTO updateOrderStatus(String orderId, OrderStatusUpdateDTO dto); // изменение заказа (статус)

    BigDecimal calculatingDiscountPrice(BigDecimal total);

    //    OrderStatus trackOrder(Long orderId);

}