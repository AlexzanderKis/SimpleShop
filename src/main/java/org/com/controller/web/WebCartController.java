package org.com.controller.web;

import org.com.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
        Web-контроллер корзины (Thymeleaf)
        Обслуживает HTML-страницы /cart/**
        Отдаёт страницу cart.html, обрабатывает формы добавления/изменения/удаления товаров,
        очистку корзины и редиректы с flash-сообщениями

        Для REST-клиентов см. CartRestController
 */

// Web-контроллер корзины
// отдаёт HTML-страницу корзины cart.html, обрабатывает POST-формы (добавить, изменить, удалить, очистить), делает редиректы с flash-сообщениями
@Controller
@RequestMapping("/cart")
public class WebCartController {

    private final CartService cartService;

    public WebCartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String viewCart(Model model){
        model.addAttribute("cart", cartService.getCart());
        return "cart";
    }

    @GetMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            @RequestParam(required = false, defaultValue = "/catalog") String returnUrl,
                            RedirectAttributes redirectAttributes){

        try{
            cartService.addItem(productId, quantity);
            redirectAttributes.addFlashAttribute("SUCCESS_Message", "Товар успешно добавлен в корзину");
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("ERROR_Message "+ e.getMessage());
        }
        return "redirect:"+returnUrl;
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId,
                                 @RequestParam int newQuantity,
                                 RedirectAttributes redirectAttributes){
        try{
            cartService.updateQuantity(productId, newQuantity);
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("ERROR_Message " + e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam Long productId,
                             RedirectAttributes redirectAttributes) {
        cartService.removeItem(productId);
        redirectAttributes.addFlashAttribute("SUCCESS_Message", "Товар удален из корзины");
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        cartService.clearCart();
        redirectAttributes.addFlashAttribute("SUCCESS_Message", "Корзина очищена");
        return "redirect:/cart";
    }
}
