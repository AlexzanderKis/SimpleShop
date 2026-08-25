package org.com;

import org.com.exceptions.UserLoginException;
import org.com.repos.OrderRepo;
import org.com.repos.ProductRepo;
import org.com.repos.UserRepo;
import org.com.service.CartService;
import org.com.service.OrderService;
import org.com.service.ProductService;
import org.com.service.UserService;

import java.math.BigDecimal;

/**
 * Примеры возможностей программы:
 *
 * Вывод доступных для покупки товаров
 * Фильтрация товаров по ключевым словам, ценам, производителям
 * Составление продуктовой корзины пользователя
 * Трекинг заказа в системе доставки
 * Возврат заказа, повтор заказа
 *
 * Система рейтинга для товаров
 * Простая рекомендательная система для покупок
 */
public class Main { // StartShop
    public static void main(String[] args) {
        System.out.print("""
                    <*Shop is Open*>
                """);

        // Репозитории
        UserRepo userRepo = new InMemoryUserRepo();
        OrderRepo orderRepo = new InMemoryOrderRepo();
        ProductRepo productRepo = new InMemoryProductRepo();
        JsonProductLoader jsonProductLoader = new JsonProductLoader();

        // Сервисы
        CartService cartService = new Cart();
        UserService userService = new UserServiceImpl(userRepo);
        ProductService productService = new ProductServiceImpl(productRepo);
        OrderService orderService = new OrderServiceImpl(orderRepo, userRepo, productRepo);

        // загружает весь каталог товаров в память через ссылку на метод
        jsonProductLoader.loadProducts().forEach(productRepo::save);

        // register new user
        try {
            userService.registerUser("Vigor", "89031115599", "levelvogor@hotmail.com", "Qwerty123@");
        } catch (Exception e) {
            e.fillInStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        // login user
        try {
            userService.login("levelvogor@hotmail.com", "Qwerty123@");
        } catch (UserLoginException e) {
            e.fillInStackTrace();
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        // filling cart
        cartService.addItem(productRepo.findById(31L), 9);
        cartService.addItem(productRepo.findById(333L), 2);
        cartService.addItem(productRepo.findById(34L), 1);
        cartService.addItem(productRepo.findById(99L), 3);
        cartService.updateQuantity(productRepo.findById(99L), 5);

        // order product list
        System.out.print("""
                
                Order list:
                """);
        for (CartItem item : cartService.getItem()) {
            System.out.printf("""
                    %s | %s QTY * %s pc/pc -> for %s
                    """, item.getProduct().getProductName(),
                    item.getQuantity(),
                    item.getProduct().getProductPrice(),
                    item.getProduct().getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                    );
        }
        System.out.printf("""
                Total cart price is: %s
                """, cartService.getTotalPrice());

        // creating order
        Order order = orderService.createOrder(userRepo.findByEmail("levelvogor@hotmail.com").get(),
                "Ordinary street, obvious district",
                cartService);

        if (cartService.getItem().isEmpty()){
            System.out.println("Cart is empty");
        }

/**
        DiscountCalculator discountCalculator = new PercentageDiscountCalculator();
        ReviewService reviewService = new ReviewServiceImpl(productRepository);
        RecommendationService recommendationService = new RecommendationServiceImpl(productRepository, orderRepository);

        // Передаём сервисы в UI
        ConsoleUI ui = new ConsoleUI(productService, cartService, orderService, userService, reviewService, recommendationService);
        ui.start();
*/

    }
}