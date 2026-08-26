package org.com;

import org.com.repos.OrderRepo;
import org.com.repos.ProductRepo;
import org.com.repos.UserRepo;
import org.com.service.CartService;
import org.com.service.OrderService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
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

    // Валидация корзины, создание заказа с уникальным UUID, сохранение и очистка корзины
    @Override
    public Order createOrder(User user, String deliveryAddress, CartService cartService) {
        if (cartService.getItem().isEmpty()) {
            throw new IllegalStateException("?Can not proceed order. Your cart is empty!?");
        }
        List<CartItem> productListCopy = List.copyOf(cartService.getItem()); // Сохраняет товары и их количество через объекты CartItem. Делает снимок данных, вызов cartService.clearCart() не стирает состав оформленного заказа.
//        Long userId = Long.valueOf(user.getUserId());
        OrderStatus orderStatus = OrderStatus.NEW_ORDER;
        Order order = new Order(user,
                UUID.randomUUID().toString().substring(0,23).toUpperCase(Locale.ROOT), // создание рандомного ID заказа 24 символа
                productListCopy,
                cartService.getTotalPrice(),
                cartService.getTotalPrice(), // discount price
                orderStatus,
                deliveryAddress
        );
        orderRepo.save(order);
        System.out.printf("""
                        ????????????????????????
                                *ORDER CHECK*
                        Order created: %s
                        Order status: %s
                        Ordered by user: %s | %s
                        Total order price: %s
                        Total order %% price: %s
                        ????????????????????????
                        """,
                order.getOrderID(),
                order.getOrderCurrentStatus(),
                order.getUser().getUserId(),
                order.getUser().getUserName(),
                order.getOrderTotalPrice(),
                order.getOrderTotalPriceWithDiscount());

        // clear the cart
        cartService.clearCart();
        return order;
    }

    // Поиск списка заказов пользователя
    @Override
    public List<Order> getOrderByUser(User user) {
        return List.copyOf(orderRepo.findByUserId(user.getUserId()));
    }

    // Отмена заказа. Проверка статуса, создание обновлённой копии со статусом CANCELED
    @Override
    public Order cancelOrder(Order order) {
//        if (order.getOrderCurrentStatus().equals(OrderStatus.DELIVERED)) {
//            throw new RuntimeException("You can not cancel already delivered order");
//        }
//        if (order.getOrderCurrentStatus().equals(OrderStatus.CANCELED)){
//            throw new RuntimeException("Order already canceled");
//        }
//
//        order.setOrderCurrentStatus(OrderStatus.CANCELED);
//        orderRepo.save(order);
        if (order.getOrderCurrentStatus() == OrderStatus.CANCELED || order.getOrderCurrentStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Can not proceed. No order OR order is done");
        }
        Order canceledOrder = order.newStatus(OrderStatus.CANCELED);
        orderRepo.save(canceledOrder); // сохранение копии заказа (отменённый)
        return canceledOrder;
    }

    // Повтор уже выполненного заказа. Перенос состава завершённого заказа обратно в CartService
    @Override
    public CartService repeatOrder(Order previousOrder, CartService cartService) {
//        Order orderRepeat = new Order(previousOrder.getUser(),
//                UUID.randomUUID().toString(),
//                previousOrder.getOrderProductList(),
//                previousOrder.getOrderTotalPrice(),
//                previousOrder.getOrderTotalPriceWithDiscount(),
//                OrderStatus.NEW_ORDER,
//                previousOrder.getOrderDeliveryAddress());
//        orderRepo.save(orderRepeat);
//        return orderRepeat;

        for (CartItem item : previousOrder.getOrderProductList()){
            // Передавая 1, мы добавляем каждую единицу товара из списка,
            // а метод addItem внутри корзины сам объединит одинаковые товары и увеличит их количество
            int repeatProductQuantity = 1;
            cartService.addItem(item.getProduct(), repeatProductQuantity);
        }
        return cartService;
    }

    // Возврат зафиксированной стоимости заказа и расчёт стоимости со скидкой
    @Override
    public BigDecimal totalOrderPrice(Order order) {
        BigDecimal discountPrice = BigDecimal.valueOf(90999.99);
        if (order.getOrderTotalPrice().compareTo(discountPrice) >= 0) {
            System.out.printf("order total price with %% %s", order.getOrderTotalPriceWithDiscount());
            return order.getOrderTotalPriceWithDiscount();
        }

        System.out.printf("order total price %s", order.getOrderTotalPrice());
        return order.getOrderTotalPrice();
    }

//    @Override
//    public void clearOrder(Cart cart) {
//        cart.clearCart();
//    }
}