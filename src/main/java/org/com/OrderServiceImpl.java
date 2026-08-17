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

    // Валидация корзины, создание заказа с уникальным UUID, сохранение и очистка корзины
    @Override
    public Order createOrder(User user, String deliveryAddress, CartService cartService) {

        if (cartService.getItem().isEmpty()) {
            throw new IllegalStateException("Can not proceed order. Your cart is empty!");
        }
//        Long userId = Long.valueOf(user.getUserId());
        OrderStatus orderStatus = OrderStatus.NEW_ORDER;

        Order order = new Order(user,
                UUID.randomUUID().toString(), // создание рандомного ID
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

        for (Product product : previousOrder.getOrderProductList()){
            // Передавая 1, мы добавляем каждую единицу товара из списка,
            // а метод addItem внутри корзины сам объединит одинаковые товары и увеличит их количество
            int repeatProductQuantity = 1;
            cartService.addItem(product, repeatProductQuantity);
        }
        return cartService;
    }

    // Возврат зафиксированной стоимости заказа // TODO расчёт с учётом скидки
    @Override
    public BigDecimal totalOrderPrice(Order order) {
//        if (discount exist){
//            return order.getOrderTotalPriceWithDiscount();
//        }
        return order.getOrderTotalPrice();
    }

//    @Override
//    public void clearOrder(Cart cart) {
//        cart.clearCart();
//    }
}