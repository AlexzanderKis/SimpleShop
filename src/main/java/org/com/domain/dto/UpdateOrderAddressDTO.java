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
@Schema(description = "Запрос на изменение адреса доставки заказа")
public class UpdateOrderAddressDTO {

    @Schema(description =  "Новый адрес доставки", example = "г. Санкт-Петербург, Невский пр-т, д. 22")
    @NotBlank(message = "Адрес доставки не может быть пустым")
    private String newDeliveryAddress;
}
