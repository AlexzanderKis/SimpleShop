package org.com;

import org.com.repos.OrderRepo;
import org.com.repos.ProductRepo;
import org.com.repos.UserRepo;
import org.com.service.CartService;
import org.com.service.OrderService;
import org.com.service.ProductService;
import org.com.service.UserService;

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
        System.out.println("Hello, World!");

/**
        // Репозитории
        ProductRepo productRepository = new InMemoryProductRepository();
        UserRepo userRepository = new InMemoryUserRepository();
        OrderRepo orderRepository = new InMemoryOrderRepository();

        // Сервисы
        ProductService productService = new ProductServiceImpl(productRepository);
        CartService cartService = new CartServiceImpl();
        OrderService orderService = new OrderServiceImpl(orderRepository, productRepository, userRepository);
        DiscountCalculator discountCalculator = new PercentageDiscountCalculator();
        UserService userService = new UserServiceImpl(userRepository);
        ReviewService reviewService = new ReviewServiceImpl(productRepository);
        RecommendationService recommendationService = new RecommendationServiceImpl(productRepository, orderRepository);
*/
        // Передаём сервисы в UI
//        ConsoleUI ui = new ConsoleUI(productService, cartService, orderService, userService, reviewService, recommendationService);
//        ui.start();

    }
}