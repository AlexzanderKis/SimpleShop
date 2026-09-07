package org.com.service;

import org.com.*;

import java.math.BigDecimal;
import java.util.List;

// сервис для создания заказа из текущей корзины пользователя.

public interface OrderService {
    Order createOrder(User user, String deliveryAddress, CartService cartService); // можно передать корзину

    Order repeatOrder(Order previousOrder, CartService cartService);

//    OrderStatus trackOrder(Long orderId);

    List<Order> getOrderByUser(User user);

    Order updateOrder(Order order); // изменение заказа (статус, адрес доставки)

    BigDecimal totalOrderPrice(Order order);

    Order cancelOrder(Order order);
}