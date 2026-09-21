package org.com;

import org.com.repos.OrderRepo;
import org.com.repos.ProductRepo;
import org.com.repos.UserRepo;
import org.com.service.CartService;
import org.com.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
                deliveryAddress,
                LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/dd/MM | hh:mm:ss a")), DateTimeFormatter.ofPattern("yyyy/dd/MM | hh:mm:ss a")));
        orderRepo.save(order);
        System.out.printf("""
                        ??????????????????????????????????????
                                *ORDER CHECK*
                        Order created: %s
                        Order date: %s
                        Order status: %s
                        Ordered by user: %s | %s
                        Total order price: %s
                        Total order %% price: %s
                        Delivery address: %s
                        ??????????????????????????????????????
                        """,
                order.getOrderID(),
                order.getOrderCreationDate(),
                order.getOrderCurrentStatus(),
                order.getUser().getUserId(),
                order.getUser().getUserName(),
                order.getOrderTotalPrice(),
                order.getOrderTotalPriceWithDiscount(),
                order.getOrderDeliveryAddress());

// clear the cart
        cartService.clearCart();
        return order;
    }

// Поиск списка заказов пользователя
    @Override
    public List<Order> getOrderByUser(User user) {
        List<Order> userOrders = new ArrayList<>(orderRepo.findByUserId(user.getUserId()));
        userOrders.sort(Comparator.comparing(Order::getOrderCreationDate));
        return List.copyOf(userOrders);
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
            throw new IllegalStateException("""
                          ?CAN NOT PROCEED?
                    Order has been already canceled
                                OR
                    Order is already delivered
                    """);
        }
        Order canceledOrder = order.setNewStatus(OrderStatus.CANCELED);
        orderRepo.save(canceledOrder); // сохранение копии заказа (отменённый)
        return canceledOrder;
    }

// Повтор уже выполненного заказа. Перенос состава завершённого заказа обратно в CartService
    @Override
    public Order repeatOrder(Order previousOrder, CartService cartService) {
//        Order orderRepeat = new Order(previousOrder.getUser(),
//                UUID.randomUUID().toString(),
//                previousOrder.getOrderProductList(),
//                previousOrder.getOrderTotalPrice(),
//                previousOrder.getOrderTotalPriceWithDiscount(),
//                OrderStatus.NEW_ORDER,
//                previousOrder.getOrderDeliveryAddress());
//        orderRepo.save(orderRepeat);
//        return orderRepeat;
        List<CartItem> previousOrderList = List.copyOf(previousOrder.getOrderProductList());
        Order repeatedOrder = new Order(previousOrder.getUser(),
                UUID.randomUUID().toString().substring(0,23).toUpperCase(Locale.ROOT), // создание рандомного ID заказа 24 символа
                previousOrderList,
                previousOrder.getOrderTotalPrice(),
                previousOrder.getOrderTotalPriceWithDiscount(), // discount price
                OrderStatus.CREATED,
                previousOrder.getOrderDeliveryAddress(),
                LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/dd/MM | hh:mm:ss a")), DateTimeFormatter.ofPattern("yyyy/dd/MM | hh:mm:ss a")));
        orderRepo.save(repeatedOrder);
//        return cartService;
        for (CartItem item : previousOrder.getOrderProductList()){
            // Передавая 1, мы добавляем каждую единицу товара из списка по 1 штуке,
            // а метод addItem внутри корзины сам объединит одинаковые товары и увеличит их количество
            // int repeatProductQuantity = 1;
            cartService.addItem(item.getProduct(), item.getQuantity());
        }
        return repeatedOrder;
    }

// Обновление информации в заказе после изменений
    @Override
    public Order updateOrder(Order order) {
        orderRepo.save(order);
        return order;
    }

// Возврат зафиксированной стоимости заказа и расчёт стоимости со скидкой
    @Override
    public BigDecimal totalOrderPrice(Order order) {
        BigDecimal discountPrice = BigDecimal.valueOf(90999.99);
        if (order.getOrderTotalPrice().compareTo(discountPrice) >= 0) {
//            System.out.printf("""
//                    ?Order total price with %% %s?
//                    """, order.getOrderTotalPriceWithDiscount());
            return order.getOrderTotalPriceWithDiscount();
        }

//        System.out.printf("""
//                ?Order total price %s?
//                """, order.getOrderTotalPrice());
        return order.getOrderTotalPrice();
    }

//    @Override
//    public void clearOrder(Cart cart) {
//        cart.clearCart();
//    }
}