package org.com.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Элемент корзины покупок (1 продукт в корзине)")
public class CartItemDTO {

    @Schema(description = "ID продукта", example = "1")
    private Long productId;

    @Schema(description = "Название продукта", example = "Яблоки Гала Крымские")
    private String productName;

    @Schema(description = "Категория продукта")
    private String category;

    @Schema(description = "Бренд продукта")
    private String brand;

    @Schema(description = "Цена за единицу (руб.)", example = "149.90")
    private BigDecimal unitPrice;

    @Schema(description = "Количество", example = "2")
    @NotNull(message = "Количество обязательно для отображения в корзине")
    @Min(value = 1, message = "Количество должно быть не менее 1")
    private int quantity;

    @Schema(description = "Стоимость позиции (цена * кол-во)", example = "299.80")
    private BigDecimal subtotal;

    @Schema(description = "URL изображения")
    private String imageURL;

    public CartItemDTO(Long productId, String productName, String category, String brand, BigDecimal unitPrice, int quantity, String imageURL) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.brand = brand;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.subtotal = unitPrice != null ? unitPrice.multiply(BigDecimal.valueOf(quantity)) : BigDecimal.ZERO;
        this.imageURL = imageURL;
    }

    // TODO: CartDto и CartItemDto содержат вычисляемые поля, которые ломаются при setItems/setQuantity
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (this.unitPrice != null){
            this.subtotal = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}