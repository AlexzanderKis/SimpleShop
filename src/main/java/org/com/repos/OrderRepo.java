package org.com.repos;

import org.com.Order;

import java.util.Optional;

public interface OrderRepo {
    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findByUserId(Long userId);
}
