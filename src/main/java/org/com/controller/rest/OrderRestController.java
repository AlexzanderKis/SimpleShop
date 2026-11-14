package org.com.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.com.domain.dto.CreateOrderDTO;
import org.com.domain.dto.OrderResponseDTO;
import org.com.domain.dto.OrderStatusUpdateDTO;
import org.com.domain.dto.UpdateOrderAddressDTO;
import org.com.service.OrderService;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.expression.Lists;

import java.util.List;

/**
    REST-контроллер заказов
    Обслуживает JSON-запросы к /api/v1/orders/**
    Позволяет оформлять заказы, просматривать свои и все заказы (админ), отменять, повторять
    менять адрес доставки и статус (админ)
    Для HTML-страниц заказов см. WebOrderController
 */
// создание заказа из корзины, список своих заказов, все заказы (только админ), заказ по ID, отмена, повтор, смена адреса, смена статуса (только админ)
// возвращает JSON
@RestController
@RequestMapping("/api/v1/orders")
public class OrderRestController {
    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Оформить заказ из корзины", description = "Фиксирует снимок позиций корзины, рассчитывает сумму и скидку, очищает корзину и присваивает статус NEW_ORDER")
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        OrderResponseDTO createdOrder = orderService.createOrder(userDetails.getUsername(), createOrderDTO);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Список заказов текущего пользователя", description = "Возвращает хронологический список заказов текущего аутентифицированного пользователя")
    public ResponseEntity<List<OrderResponseDTO>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(orderService.getOrderByUserEmail(userDetails.getUsername()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Все заказы магазина (ADMIN ONLY)", description = "Просмотр всех заказов всех клиентов магазина")
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders(){
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Получить заказ по ID", description = "Возвращает подробный снимок заказа со всеми позициями и текущим статусом")
    public ResponseEntity<OrderResponseDTO> getOrderBy(@PathVariable String orderId){
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/{orderId}/cancel")
    @Operation(summary = "Отменить заказ", description = "Переводит заказ в статус CANCELED. Отмена невозможна, если заказ уже отменен или доставлен.")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable String orderId,
                                                        @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(orderService.cancelOrder(orderId, userDetails.getUsername()));
    }

    /**
            Повторяет предыдущий заказ
            !ВАЖНО! метод HE создаёт новый заказ сразу:
            1. Берёт позиции из указанного заказа
            2. Добавляет их в текущую корзину пользователя
            3. Создаёт черновик нового заказа
            Пользователь должен затем самостоятельно подтвердить оформление

            @param orderId ID старого заказа
            @param userDetails текущий аутентифицированный пользователь (заказ должен принадлежать ему)
            @return возвращает черновик нового заказа
     */
    @GetMapping("/{orderId}/address")
    @Operation(summary = "Изменить адрес доставки", description = "Обновляет адрес доставки у оформленного заказа")
    public ResponseEntity<OrderResponseDTO> updateAddress(@PathVariable String orderId,
                                                          @Valid @RequestBody UpdateOrderAddressDTO updateOrderAddressDTO,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(orderService.updateOrderDeliveryAddress(orderId,updateOrderAddressDTO, userDetails.getUsername()));
    }

    @GetMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Изменить статус заказа (ADMIN ONLY)", description = "Смена статуса жизненного цикла заказа (PROCESSING, PAID, DELIVERED и др.)")
    public ResponseEntity<OrderResponseDTO> updateStatus(@PathVariable String orderId,
                                                         @Valid @RequestBody OrderStatusUpdateDTO orderStatusUpdateDTO){
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId,orderStatusUpdateDTO));
    }
}
