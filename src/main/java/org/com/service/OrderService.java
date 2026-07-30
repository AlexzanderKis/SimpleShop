package org.com.service;

import org.com.Order;
import org.com.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user, String deliveryAddress, CartService cartService);// можно передать корзину

    void cancelOrder(Order order);

    Order repeatOrder(Order previousOrder);

//    OrderStatus trackOrder(Long orderId);

    List<Order> getOrderByUser(User user);
}
