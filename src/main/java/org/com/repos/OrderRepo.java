package org.com.repos;

import org.com.Order;
import org.com.User;

import java.util.List;

// Репозиторий OrderRepo отвечает за хранение и поиск объектов Order

public interface OrderRepo {
    void save(Order order);

    List<Order> findById(String id);

    List<Order> findByUserId(String userId);

    List<Order> findByUser(User user);
}