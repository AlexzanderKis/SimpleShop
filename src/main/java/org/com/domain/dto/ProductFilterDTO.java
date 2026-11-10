package org.com.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.com.domain.entity.enums.CategoryOfProd;

import java.math.BigDecimal;

// Это внутренний DTO для передачи параметров фильтрации из контроллера в сервис
// Фактически это "форма параметров запроса", которая удобнее, чем передавать 4 отдельных аргумента
// Для фильтрации = keywords, category, minPrice, maxPrice
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Параметры фильтрации каталога продуктов")
public class ProductFilterDTO {
    @Schema(description = "Ключевое слово для поиска", example = "яблоко")
    private String keywords;

    @Schema(description = "Категория", example = "FRUIT")
    private CategoryOfProd category;

    @Schema(description = "Минимальная цена", example = "50.00")
    private BigDecimal minPrice;

    @Schema(description = "Максимальная цена", example = "500.00")
    private BigDecimal maxPrice;
}
