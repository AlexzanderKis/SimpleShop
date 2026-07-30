package org.com;

import org.com.repos.OrderRepo;
import org.com.repos.ProductRepo;
import org.com.repos.UserRepo;
import org.com.service.CartService;
import org.com.service.OrderService;

import java.math.BigDecimal;
import java.util.List;

public class OrderServiceImpl implements OrderService {
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;

    public OrderServiceImpl(OrderRepo orderRepo, UserRepo userRepo, ProductRepo productRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
    }

    public OrderRepo getOrderRepo() {
        return orderRepo;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public ProductRepo getProductRepo() {
        return productRepo;
    }

    @Override
    public Order createOrder(User user, String deliveryAddress, CartService cartService) {
//        OrderStatus.NEW_ORDER;
//        OrderStatus.CREATED;
//        OrderStatus.CANCELED;
//        OrderStatus.DELIVERED;
//        OrderStatus.PAID;
//        OrderStatus.NOT_PAID;
        return null;
    }

    @Override
    public Order repeatOrder(Order previousOrder) {
        return null;
    }

    @Override
    public List<Order> getOrderByUser(User user) {
        return List.of();
    }

    @Override
    public BigDecimal totalOrderPrice(Order order) {
        return null;
    }

    @Override
    public void cancelOrder(Order order) {

    }

    @Override
    public void clearOrder(Order order) {

    }
}