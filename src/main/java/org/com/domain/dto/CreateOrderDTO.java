package org.com.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос на создание нового заказа (нужен адрес)")
public class CreateOrderDTO {

    @Schema(description = "Адрес доставки", example = "г. Москва, ул. Тверская, д. 12, кв. 45")
    @NotBlank(message = "Адрес доставки не может быть пустым")
    private String deliveryAddress;
}
