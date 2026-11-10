package org.com.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.domain.entity.enums.OrderStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос на обновление статуса заказа (ADMIN ONLY)")
public class OrderStatusUpdateDTO {

    @Schema(description = "Новый статус заказа", example = "PROCESSING")
    @NotNull(message = "Статус заказа обязателен")
    private OrderStatus newStatus;
}
