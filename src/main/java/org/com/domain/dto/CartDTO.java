package org.com.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Состояние корзины покупок (items, totalPrice, totalQuantity)")
public class CartDTO {

    @Schema(description = "Список товаров в корзине")
    private List<CartItemDTO> items = new ArrayList<>();

    @Schema(description = "Общая стоимость корзины", example = "588.70")
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Schema(description = "Общее количество единиц товаров", example = "4")
    private int totalQuantity = 0;

    // TODO: CartDto и CartItemDto содержат вычисляемые поля, которые ломаются при setItems/setQuantity
    public CartDTO(List<CartItemDTO> items) {
        if (items != null){
            this.items = items;
            this.totalPrice = items.stream()
                    .map(CartItemDTO::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.totalQuantity = items.stream()
                    .mapToInt(CartItemDTO::getQuantity)
                    .sum();
        }
    }
}
