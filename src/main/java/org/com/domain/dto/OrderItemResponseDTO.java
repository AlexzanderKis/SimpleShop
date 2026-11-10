package org.com.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Позиция в сохраненном заказе (клиенту = id, prodID, prodName, prodBrand, unitPrice, quantity, subtotal)")
public class OrderItemResponseDTO {

    @Schema(description = "ID позиции", example = "1")
    private Long id;

    @Schema(description = "ID исходного продукта (если еще существует)", example = "1")
    private Long productId;

    @Schema(description = "Снэпшот названия продукта на момент заказа", example = "Яблоки Гала Крымские")
    private String productName;

    @Schema(description = "Снэпшот бренда на момент заказа", example = "Сады Крыма")
    private String productBrand;

    @Schema(description = "Снэпшот цены за единицу на момент заказа", example = "149.90")
    private BigDecimal unitPrice;

    @Schema(description = "Набранное количество", example = "2")
    private int quantity;

    @Schema(description = "Итоговая сумма по 1 позиции", example = "299.80")
    private BigDecimal subtotal;
}
