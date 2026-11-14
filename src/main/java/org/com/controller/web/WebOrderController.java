package org.com.controller.web;

import org.com.domain.dto.CreateOrderDTO;
import org.com.domain.dto.OrderResponseDTO;
import org.com.domain.dto.UpdateOrderAddressDTO;
import org.com.service.CartService;
import org.com.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
        Web-контроллер заказов (Thymeleaf)
        Обслуживает HTML-страницы /orders/**
        Отдаёт HTML-страницы списка заказов, оформления заказа (checkout) и деталей заказа,
        обрабатывает формы оформления,
        отмены заказа,
        повтора и смены адреса доставки,
        делает редиректы с flash-сообщениями

        Для REST-клиентов см. OrderRestController.
 */

@Controller
@RequestMapping("/orders")
public class WebOrderController {
    private final OrderService orderService;
    private final CartService cartService;

    public WebOrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }
    @GetMapping
    public String listOrders(@AuthenticationPrincipal UserDetails userDetails, Model model){
        List<OrderResponseDTO> orders = orderService.getOrderByUserEmail(userDetails.getUsername());

        model.addAttribute("orders", orders);
        model.addAttribute("cartItemCount", cartService.getCart().getTotalQuantity());

        return "orders";
    }

    @GetMapping("/checkout")
    public String showCheckout(@AuthenticationPrincipal UserDetails userDetails, Model model){
        if (cartService.getItems().isEmpty()){
            return "redirect:/cart";
        }

        model.addAttribute("cart", cartService.getCart());
        model.addAttribute("discountTotal", orderService.calculatingDiscountPrice(cartService.getTotalPrice()));
        model.addAttribute("createOrderDTO", new CreateOrderDTO());

        return "checkout";
    }

    /**
        Обрабатывает форму оформления заказа
        В случае успеха редиректит на страницу созданного заказа
        В случае любой ошибки (пустая корзина, проблемы с адресом и т.п.) делает редирект обратно на /orders/checkout и кладёт сообщение в flash-атрибуты

        Пользователь не видит стек-трейс - только сообщение
     */
    @GetMapping("/checkout")
    public String processCheckout(@AuthenticationPrincipal UserDetails userDetails,
                                  @ModelAttribute("createOrderDTO") CreateOrderDTO createOrderDTO,
                                  RedirectAttributes redirectAttributes){

        try {
            OrderResponseDTO order = orderService.createOrder(userDetails.getUsername(), createOrderDTO);
            redirectAttributes.addFlashAttribute("SUCCESS_Message","Заказ #" + order.getOrderId() + " успешно оформлен!");
            return "redirect:/orders/" + order.getOrderId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("ERROR_Message "+e.getMessage());
            return "redirect:/orders/checkout";
        }
    }

    @GetMapping("/{orderId}")
    public String showOrderDetails(@PathVariable String orderId, Model model){
        OrderResponseDTO order = orderService.getOrderById(orderId);

        model.addAttribute("order",order);
        model.addAttribute("cartItemCount",cartService.getCart().getTotalQuantity());

        return "order-details";
    }

    @GetMapping("/{orderId}/cancel")
    public String cancelOrder(@PathVariable String orderId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes){
        try {
            orderService.cancelOrder(orderId, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("SUCCESS_Message", "Заказ #" + orderId + " успешно отменен.");
            return "redirect:/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("ERROR_Message "+e.getMessage());
        }
        return "redirect:/orders/"+orderId;
    }

    @GetMapping("/{orderId}/repeat")
    public String repeatOrder(@PathVariable String orderId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes){
        try {
            orderService.repeatOrder(orderId, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("SUCCESS_Message","Товары из заказа #" + orderId + " повторно добавлены в корзину.");
            return "redirect:/cart";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("ERROR_Message "+e.getMessage());
            return "redirect:/orders/";
        }
    }

    /**
        Обновляет адрес доставки в заказе
        Принимает новое значение адреса newAddress как обычный @RequestParam (не JSON),
        оборачивает его в new UpdateOrderAddressDto() и проверяет, что заказ принадлежит текущему пользователю (внутри OrderService)
        При ошибке - редирект обратно на детали заказа с flash-сообщением

     почему не @ModelAttribute потому что форма отправляет одно поле
     */
    @GetMapping("/{orderId}/address")
    public String updateAddress(@AuthenticationPrincipal UserDetails userDetails,
                                @PathVariable String orderId,
                                @RequestParam String newAddress,
                                RedirectAttributes redirectAttributes){
        try {
            orderService.updateOrderDeliveryAddress(orderId,new UpdateOrderAddressDTO(newAddress), userDetails.getUsername());
            redirectAttributes.addFlashAttribute("SUCCESS_Message","Адрес доставки успешно обновлен.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("ERROR_Message "+e.getMessage());
//            return "redirect:/order-details";
        }
        return "redirect:/orders/"+orderId;
    }
}
