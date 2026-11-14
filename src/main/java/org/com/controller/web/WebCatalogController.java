package org.com.controller.web;

import org.com.domain.dto.ProductDTO;
import org.com.domain.dto.ProductFilterDTO;
import org.com.domain.entity.enums.CategoryOfProd;
import org.com.service.CartService;
import org.com.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
         Web-контроллер каталога товаров (Thymeleaf)
         Обслуживает HTML-страницы /, /catalog и /products/{id}
         Отдаёт страницу каталога со списком товаров и фильтрами, а также карточку товара

         Для REST-клиентов см. ProductRestController
 */

// Web-контроллер каталога
// отдаёт HTML-страницы каталога catalog.html и карточки товара product-details.html,
// применяет фильтры (keyword, category, minPrice, maxPrice), передаёт данные в шаблон
@Controller
public class WebCatalogController {

    private final ProductService productService;
    private final CartService cartService;

    public WebCatalogController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping({"/", "/catalog"})
    public String showCatalog(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false)CategoryOfProd category,
                              @RequestParam(required = false)BigDecimal minPrice,
                              @RequestParam(required = false)BigDecimal maxPrice,
                              Model model){

        ProductFilterDTO filterDTO = new ProductFilterDTO(keyword,category,minPrice,maxPrice);
        List<ProductDTO> products = productService.filter(filterDTO);

        model.addAttribute("products",products);
        model.addAttribute("categories",productService.getAllCategories());
        model.addAttribute("currentFilter", filterDTO);
        model.addAttribute("cartItemCount", cartService.getCart().getTotalQuantity());
        model.addAttribute("cartTotal", cartService.getCart().getTotalPrice());

        return "catalog";
    }

    /**
        Отображает карточку товара
        Если товар с указанным ID не найден - выбрасывает IllegalArgumentException,
        который перехватывается обработчиком исключений и превращается в страницу 404 (см. GlobalExceptionHandler)
     */
    @GetMapping("/products/{id}")
    public String showProductDetails(@PathVariable Long id, Model model){
        ProductDTO product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден"));

        model.addAttribute("product", product);
        model.addAttribute("cartItemCount", cartService.getCart().getTotalQuantity());

        return "product-details";

    }
}