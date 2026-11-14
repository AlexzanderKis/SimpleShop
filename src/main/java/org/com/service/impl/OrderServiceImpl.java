package org.com.service.impl;

import org.com.domain.dto.*;
import org.com.domain.entity.OrderEntity;
import org.com.domain.entity.OrderItemEntity;
import org.com.domain.entity.ProductEntity;
import org.com.domain.entity.UserEntity;
import org.com.domain.entity.enums.OrderStatus;
import org.com.exceptions.ResourceNotFoundException;
import org.com.repos.OrderRepo;
import org.com.repos.ProductRepo;
import org.com.repos.UserRepo;
import org.com.service.CartService;
import org.com.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
         Реализация сервиса заказов
         Управляет жизненным циклом заказа: создание из корзины с снэпшотом цен, отмена, повтор, смена адреса и статуса
         Класс помечен @Transactional: все изменения в БД выполняются в транзакции, read-методы - readOnly = true (оптимизация Hibernate)

         Скидка 10% применяется при сумме заказа >= 90 999.99 руб. (см. DISCOUNT_THRESHOLD)
         ID заказа генерируется приложением в формате ORD-XXXXXXXXXXXXXXXXXXX (19 символов UUID)
 */
@Service
@Transactional
// все методы класса по умолчанию выполняются в одной транзакции. Если внутри метода произойдёт исключение -> все изменения в БД откатятся
public class OrderServiceImpl implements OrderService {

    /**
     * если сумма заказа ≥ 90 999.99 руб., применяется скидка 10%.
     * TODO лучше вынести в конфигурацию (application.yml), чтобы менять без пересборки
     */
    private static final BigDecimal DISCOUNT_THRESHOLD = BigDecimal.valueOf(90999.99);
    private static final BigDecimal DISCOUNT_PERCENTAGE = BigDecimal.valueOf(0.10); // 10% discount
    private static final String ORDER_ID_HEAD = "ORD-";

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepo orderRepo, UserRepo userRepo,
                            ProductRepo productRepo, CartService cartService) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.cartService = cartService;
    }

    // Что бы не повторять создание заказа
    private OrderEntity getOrderORThrow(String orderId){
        return orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ с ID " + orderId + " не найден"));
    }

    /**
     * Создаёт заказ из текущей корзины пользователя
     * 1. Загружает корзину пользователя; если пуста - исключение
     * 2. Фиксирует snapshot цен и названий товаров - цены в заказе НЕ зависят от будущих изменений цены товара
     * 3. Рассчитывает итоговую сумму и скидку
     * 4. Создаёт заказ со статусом NEW_ORDER
     * 5. Очищает корзину пользователя
     *
     * @param userEmail      email пользователя
     * @param createOrderDTO данные для оформления (адрес доставки и т.п.)
     * @return DTO созданного заказа
     * @throws IllegalStateException если корзина пуста
     */
    // Валидация корзины, создание заказа с уникальным UUID, сохранение и очистка корзины
    @Override
    public OrderResponseDTO createOrder(String userEmail, CreateOrderDTO createOrderDTO) {
        // Валидация адреса
        // Дублирует @NotBlank на CreateOrderDTO
        // Это защита в глубину: даже если валидация на контроллере будет отключена (например, при вызове из другого сервиса), метод не пропустит пустой адрес
        if (createOrderDTO.getDeliveryAddress() == null || createOrderDTO.getDeliveryAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Адрес доставки не может быть пустым");
        }
        // Сохраняет товары и их количество через объекты CartItemDTO
        List<CartItemDTO> cartItems = cartService.getItems();

        // Делает снимок данных, вызов cartService.clearCart() не стирает состав оформленного заказа
//        List<CartItemDTO> cartItems = List.copyOf(cartService.getItems());
        // Проверка корзины
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Невозможно оформить заказ: Ваша корзина пуста");
        }

        // Загрузка пользователя
        UserEntity user = userRepo.findByEmail(userEmail.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        // Расчёт цены корзины и скидки
        BigDecimal totalPrice = cartService.getTotalPrice();
        BigDecimal totalPriceWithDiscount = calculatingDiscountPrice(totalPrice);

        // ГЕнерация уникального ID заказа в 24 символа
        String orderId = ORDER_ID_HEAD + UUID.randomUUID().toString().replace("-", "").substring(0, 19).toUpperCase(Locale.ROOT);

        // Создаёт entity заказа
        OrderEntity order = new OrderEntity(
                orderId,
                user,
                totalPrice,
                totalPriceWithDiscount,
                OrderStatus.NEW_ORDER,
                createOrderDTO.getDeliveryAddress().trim(),
                LocalDateTime.parse(LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy/dd/MM | hh:mm:ss a")),
                        DateTimeFormatter.ofPattern("yyyy/dd/MM | hh:mm:ss a")));

        // Снэпшот позиций заказа
        for (CartItemDTO cartItem : cartItems) {
            ProductEntity product = productRepo.findById(cartItem.getProductId()).orElse(null);
            OrderItemEntity orderItem = new OrderItemEntity(
                    order,
                    product,
                    cartItem.getProductName(),
                    cartItem.getBrand(),
                    cartItem.getUnitPrice(),
                    cartItem.getQuantity());

            order.addItem(orderItem);
        }

        // Сохранение заказа
        OrderEntity orderEntitySaved = orderRepo.save(order);

/**
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
 );
 */

        // Очистка корзины
        cartService.clearCart();
        return mapToDTO(orderEntitySaved);
    }

    // Ищет заказ по ID
    @Override
    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderById(String orderId) {
//        OrderEntity order = orderRepo.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Заказ с ID " + orderId + " не найден"));
        OrderEntity order = getOrderORThrow(orderId);
        return mapToDTO(order);
    }

    // Ищет заказ по email пользователя
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrderByUserEmail(String userEmail) {
        return orderRepo.findByUserEmailOrderByCreatedAtDesc(userEmail.trim().toLowerCase(Locale.ROOT))
                .stream().map(this::mapToDTO).toList();
    }

    // Показывает все заказы магазина (ADMIN ONLY)
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepo.findAll().stream().map(this::mapToDTO).toList();
    }

    // Отмена заказа. Проверка статуса, создание обновлённой копии со статусом CANCELED
    @Override
    public OrderResponseDTO cancelOrder(String orderId, String userEmail) {
//        OrderEntity order = orderRepo.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Заказ #" + orderId + " не найден"));
        OrderEntity order = getOrderORThrow(orderId);

        if (!order.getUser().getEmail().toLowerCase(Locale.ROOT).equals(userEmail.trim().toLowerCase(Locale.ROOT))) {
            throw new IllegalStateException("У вас нет прав на отмену чужого заказа");
        }

/**
 if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.DELIVERED) {
 throw new IllegalStateException("Невозможно отменить заказ: он уже был отменен либо доставлен");
 }
 */

        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new IllegalStateException("Невозможно отменить заказ: он уже был отменен");
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Невозможно отменить заказ: он уже был доставлен");
        }

        order.setStatus(OrderStatus.CANCELED);
        OrderEntity canceledOrder = orderRepo.save(order);
        orderRepo.save(canceledOrder); // сохранение копии заказа (отменённый)

        return mapToDTO(canceledOrder);
    }

    /**
             Повторяет заказ
             ВАЖНО: метод делает две вещи:
             1. Копирует товары из старого заказа в текущую корзину (с актуальными ценами из каталога)
             2. Создаёт новый заказ со статусом CREATED и снимком старых цен
             Это позволяет пользователю либо сразу увидеть «черновик», либо пойти в корзину и оформить заново

             @param orderId   ID старого заказа
             @param userEmail email пользователя (для проверки прав)
             @return DTO нового заказа со статусом CREATED
             <p>
         TODO
             Стоит пересмотреть: возможно, достаточно только наполнять корзину
             Либо repeatOrder только наполняет корзину и возвращает CartDto, а не OrderResponseDto
             Либо создаёт заказ со статусом NEW_ORDER, но тогда корзину наполнять не нужно
     */
    // Повтор уже выполненного заказа. Перенос состава завершённого заказа обратно в CartService
    @Override
    public OrderResponseDTO repeatOrder(String orderId, String userEmail) {
//        OrderEntity previousOrder = orderRepo.findById(orderId) // прошлый заказ который повторяем
//                .orElseThrow(() -> new ResourceNotFoundException("Заказ #" + orderId + " не найден"));

        OrderEntity previousOrder = getOrderORThrow(orderId); // прошлый заказ который повторяем;

        // Кладём все товары обратно в корзину
        for (OrderItemEntity item : previousOrder.getItems()) {
            if (item.getProduct() != null) {
                cartService.addItem(item.getProduct().getId(), item.getQuantity());
            }
        }

        // Вернуть снэпшот повторенного заказа с статусом CREATED если не оформлен
        String newOrderId = ORDER_ID_HEAD + UUID.randomUUID().toString().replace("-", "").substring(0, 19).toUpperCase(Locale.ROOT);
        OrderEntity repeatedOrder = new OrderEntity(
                newOrderId,
                previousOrder.getUser(),
                previousOrder.getTotalPrice(),
                previousOrder.getTotalPriceWIthDiscount(),
                OrderStatus.CREATED,
                previousOrder.getDeliveryAddress(),
                LocalDateTime.now()); // TODO Формат сменить

        // Собираем товары из прошлого
        for (OrderItemEntity prevItem : previousOrder.getItems()) {
            OrderItemEntity newItem = new OrderItemEntity(
                    repeatedOrder,
                    prevItem.getProduct(),
                    prevItem.getProductNameSnapshot(),
                    prevItem.getProductBrandSnapshot(),
                    prevItem.getUnitPriceSnapshot(),
                    prevItem.getQuantity());

            // Добавляем товары из прошлого
            repeatedOrder.addItem(newItem);
        }
        return mapToDTO(repeatedOrder);
    }

    // Поиск списка заказов пользователя
//    @Override
//    public List<OrderEntity> getOrderByUser(UserResponseDTO user) {
//        List<OrderEntity> userOrders = new ArrayList<>(orderRepo.findByUserIdOrderByCreatedAtDesc(user.getId()));
//        userOrders.sort(Comparator.comparing(OrderEntity::getUser));
//        return List.copyOf(userOrders);
//    }

    // Обновление информации в заказе после изменений
    // Статус заказа меняет только админ
    @Override
    public OrderResponseDTO updateOrderStatus(String orderId, OrderStatusUpdateDTO dto) {
//        OrderEntity order = orderRepo.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Заказ #" + orderId + " не найден"));
        OrderEntity order = getOrderORThrow(orderId);
        return null;
    }

    // Обновление адреса доставки в заказе
    @Override
    public OrderResponseDTO updateOrderDeliveryAddress(String orderId, UpdateOrderAddressDTO dto, String userEmail) {
        OrderEntity order = getOrderORThrow(orderId);

        // Проверка прав редактирования
        if (!order.getUser().getEmail().toLowerCase(Locale.ROOT).equals(userEmail.trim().toLowerCase(Locale.ROOT))){
            throw new IllegalStateException("У вас нет прав на редактирование чужого заказа");
        }

        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.DELIVERED){
            throw new IllegalStateException("Нельзя изменить адрес у отмененного или доставленного заказа");
        }

        order.setDeliveryAddress(dto.getNewDeliveryAddress());
        OrderEntity savedOrder = orderRepo.save(order);
        return mapToDTO(savedOrder);
    }

    // Возврат стоимости со скидкой
    @Override
    public BigDecimal calculatingDiscountPrice(BigDecimal total) {
        if (total.compareTo(DISCOUNT_THRESHOLD) >= 0) {
            BigDecimal discount = total.multiply(DISCOUNT_PERCENTAGE);
            return total.subtract(discount).setScale(2, RoundingMode.HALF_UP);
        }
        return total;
    }

    // маппинг Entity в DTO
    private OrderResponseDTO mapToDTO(OrderEntity orderEntity) {
        List<OrderItemResponseDTO> items = orderEntity.getItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getId(),
                        item.getProduct() != null ? item.getProduct().getId() : null,
                        item.getProductNameSnapshot(),
                        item.getProductBrandSnapshot(),
                        item.getUnitPriceSnapshot(),
                        item.getQuantity(),
                        item.getSubtotal()))
                .toList();

        return new OrderResponseDTO(
                orderEntity.getId(),
                orderEntity.getUser().getId(),
                orderEntity.getUser().getName(),
                orderEntity.getUser().getEmail(),
                orderEntity.getUser().getPhoneNumber(),
                orderEntity.getStatus(),
                orderEntity.getDeliveryAddress(),
                orderEntity.getTotalPrice(),
                orderEntity.getTotalPriceWIthDiscount(),
                orderEntity.getCreatedAt(),
                items);
    }

//    @Override
//    public void clearOrder(Cart cart) {
//        cart.clearCart();
//    }
}