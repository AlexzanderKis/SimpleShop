package org.com;

import org.com.repos.OrderRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// класс, внутри которого данные хранятся в Map (в оперативной памяти)
public class InMemoryOrderRepo implements OrderRepo {

    private final Map<String, Order> ordersMap = new HashMap<>();

    @Override
    public void save(Order order) {
        ordersMap.put(order.getOrderID(), order);
    }

    @Override
    public List<Order> findById(String id) {
        return List.copyOf(ordersMap.values().stream().filter(order -> order.getOrderID().equals(id)).toList());
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return List.copyOf(ordersMap.values().stream().filter(order -> order.getUser().getUserId().equals(userId)).toList());
    }

    @Override
    public List<Order> findByUser(User user) {
        return List.copyOf(ordersMap.values().stream().filter(order -> order.getUser().equals(user)).toList());
    }
}