package org.com;

import org.com.repos.OrderRepo;
import org.com.repos.ProductRepo;
import org.com.repos.UserRepo;
import org.com.service.CartService;
import org.com.service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

        if (cartService.getItem().isEmpty()) {
            throw new IllegalStateException("Can not proceed order. Your cart is empty!");
        }

//        Long userId = Long.valueOf(user.getUserId());
        OrderStatus orderStatus = OrderStatus.NEW_ORDER;

        Order order = new Order(user,
                UUID.randomUUID().toString(),
                cartService.getItem().stream().map(CartItem::getProduct).toList(),
                cartService.getTotalPrice(),
                cartService.getTotalPrice(),
                orderStatus,
                deliveryAddress
        );
        orderRepo.save(order);
        cartService.clearCart();
        return order;
    }

    @Override
    public List<Order> getOrderByUser(User user) {
        return List.of();
    }

    @Override
    public Order repeatOrder(Order previousOrder) {
        return null;
    }

    @Override
    public BigDecimal totalOrderPrice(Order order) {
        return null;
    }

    @Override
    public void cancelOrder(Order order) {
        OrderStatus canceled = OrderStatus.CANCELED;
    }

    @Override
    public void clearOrder(Cart cart) {
        cart.clearCart();
    }
}