package org.com.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.domain.entity.enums.CategoryOfProd;

import java.math.BigDecimal;
import java.util.List;

/**
        Заделить класс
        TODO: ProductCreateDto - только поля для создания, с валидацией
        TODO: ProductResponseDto - только поля для отдачи, с id
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Данные о единице продукта каталога")
public class ProductDTO {

    @Schema(description = "Идентификатор продукта", example = "1")
    private Long id;

    @Schema(description = "Название продукта", example = "Яблоки Гала Крымские")
    private String name;

    @Schema(description = "Категория товара", example = "FRUIT")
    @NotNull(message = "Категория обязательна")
    private CategoryOfProd category;

    @Schema(description = "Бренд / Производитель", example = "Сады Крыма")
    @NotBlank(message = "Бренд обязателен")
    private String brand;

    @Schema(description = "Цена за единицу (руб.)", example = "149.90")
    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше нуля")
    private BigDecimal price;

    @Schema(description = "Ключевые слова для поиска")
    private List<String> keywords;

    @Schema(description = "Ссылка на изображение товара")
    private String imageUrl;


//    @Override
//    public String toString() {
//        return String.format("ID %s [%s] | %s, Brand: %s - %s",
//                productId,
//                productCategory,
//                productName,
//                productBrand,
//                productPrice);
////        return "ID " + productId + " | " + productCategory + " | " + productName + ", Brand: " + productBrand + " - " + productPrice;
//    }
}