package org.com;

import org.com.repos.OrderRepo;
import org.com.repos.ProductRepo;
import org.com.repos.UserRepo;
import org.com.service.CartService;
import org.com.service.OrderService;
import org.com.service.ProductService;
import org.com.service.UserService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

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

//        register user
//        String name = "Achilles";
//        String phoneNumber = "89997776655";
//        String email = "allfreetome@gmail.com";
//        String password = "qwerty123";

//        return new User();

        // Репозитории
        UserRepo userRepo = new InMemoryUserRepo();
        OrderRepo orderRepo = new InMemoryOrderRepo();
        ProductRepo productRepo = new InMemoryProductRepo();

        // Сервисы
        CartService cartService = new Cart();
        UserService userService = new UserServiceImpl(userRepo);
        ProductService productService = new ProductServiceImpl(productRepo);
        OrderService orderService = new OrderServiceImpl(orderRepo, userRepo, productRepo);


/** // Товары
        Product greenApple = new Product(31L,
                "Green Apple",
                CategoryOfProd.FRUIT,
                "Eden",
                BigDecimal.valueOf(9999.99),
                List.of("green", "apple", "eden", "divine"));
        productRepo.save(greenApple);

        Product beefJerky = new Product(33L,
                "Beef jerk-off",
                CategoryOfProd.MEAT,
                "Jerk-Off Beef",
                BigDecimal.valueOf(199.99),
                List.of("beef", "jerky", "jerk-off", "jerky"));
        productRepo.save(beefJerky);

        Product blackCoffee = new Product(39L,
                "Black arse Coffee",
                CategoryOfProd.BEVERAGES,
                "Negro de Drink",
                BigDecimal.valueOf(99.99),
                List.of("coffee", "black", "black coffee", "negro", "black drink"));
        productRepo.save(blackCoffee);

        Product cowMilk = new Product(93L,
                "Fresh Tit Cow Milk",
                CategoryOfProd.FRESH_PRODUCE,
                "TityCowy",
                BigDecimal.valueOf(399.99),
                List.of("cow", "milk", "cow milk", "tits milk", "fresh milk"));
        productRepo.save(cowMilk);

        Product greenTea = new Product(38L,
                "Green Mint Tea",
                CategoryOfProd.BEVERAGES,
                "Green Mountain",
                BigDecimal.valueOf(599.99),
                List.of("tea", "green", "green tea", "mint", "mountain", "correct drink"));
        productRepo.save(greenTea);

        Product octopus = new Product(333L,
                "Fresh Octopus",
                CategoryOfProd.SEAFOOD,
                "OctoPussy",
                BigDecimal.valueOf(99.99),
                List.of("octopus", "fresh", "sea food"));
        productRepo.save(octopus);

        Product yogurtVanilla = new Product(32L,
                "Yogurt Vanilla",
                CategoryOfProd.FRESH_PRODUCE,
                "Vanilla Puddle",
                BigDecimal.valueOf(999.99),
                List.of("vogue yogurt", "yogurt", "vanilla", "vanilla puddle"));
        productRepo.save(yogurtVanilla);

        Product redTomato = new Product(99L,
                "Red Tomato",
                CategoryOfProd.VEGETABLE,
                "Auto-Tomato",
                BigDecimal.valueOf(99.99),
                List.of("auto tomato", "tomato", "red tomato"));
        productRepo.save(redTomato);

        Product turkeyMeat = new Product(34L,
                "Turkey Meat",
                CategoryOfProd.MEAT,
                "Turkey from Turkey",
                BigDecimal.valueOf(499.99),
                List.of("meat", "turkey meat", "turkish meat", "turkish bird"));
        productRepo.save(turkeyMeat);
        */

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