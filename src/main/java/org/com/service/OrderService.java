package org.com.service;

import org.com.*;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    Order createOrder(User user, String deliveryAddress, CartService cartService);// можно передать корзину

    Order repeatOrder(Order previousOrder);

//    OrderStatus trackOrder(Long orderId);

    List<Order> getOrderByUser(User user);

    BigDecimal totalOrderPrice(Order order);

    void cancelOrder(Order order);

    void clearOrder(Order order);
}