package org.com.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.domain.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// в чеке заказа = orderId, userId, userName, userEmail, userPhoneNumber, status, deliveryAddress, totalPrice, totalPriceWithDiscount, createdAt, items
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Снэпшот оформленного заказа")
public class OrderResponseDTO {

    @Schema(description = "Уникальный буквенно-цифровой номер заказа", example = "ORD-2026-X8Y9Z1A2B3C4")
    private String orderId;

    @Schema(description = "ID пользователя-заказчика", example = "USR-DEMO-001")
    private String userId;

    @Schema(description = "Имя пользователя-заказчика", example = "Иван Петров")
    private String userName;

    @Schema(description = "Email заказчика", example = "user@example.com")
    private String userEmail;

    @Schema(description = "Телефон заказчика", example = "+7 (903) 111-55-99")
    private String userPhoneNumber;

    @Schema(description = "Текущий статус заказа", example = "NEW_ORDER")
    private OrderStatus status;

    @Schema(description = "Адрес доставки", example = "г. Москва, ул. Ленина, д. 5")
    private String deliveryAddress;

    @Schema(description = "Суммарная базовая стоимость товаров", example = "1250.00")
    private BigDecimal totalPrice;

    @Schema(description = "Стоимость с учетом скидок", example = "1250.00")
    private BigDecimal totalPriceWithDiscount;

    @Schema(description = "Дата и время создания заказа")
    private LocalDateTime createdAt;

    @Schema(description = "Список позиций заказа")
    private List<OrderItemResponseDTO> items;

//    @Override
//    public String toString() {
//        StringBuilder stringBuilder = new StringBuilder();
//        System.out.printf("""
//                OrderID: %s
//                Order date: %s
//                Current status: %s
//                Delivery address: %s
//                Total price: %s
//                """, orderID, orderCreationDate, orderCurrentStatus, orderDeliveryAddress, orderTotalPrice);
//        for (CartItem item : orderProductList) {
//            if (item.getQuantity() == 0) {
//                continue;
//            }
//            stringBuilder.append(String.format("""
//                            %s | %s - %s QTY for %s
//                            """,
//                    item.getProduct().getProductId(),
//                    item.getProduct().getProductName(),
//                    item.getQuantity(),
//                    item.getProduct().getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
//        }
//        return stringBuilder.toString();
//    }
}
