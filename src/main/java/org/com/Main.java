package org.com;

import static org.com.tools.RUPhoneNumberValidator.validateNumber;
import static org.com.tools.PasswordValidator.validatePassword;

import org.apache.commons.validator.routines.EmailValidator;

import org.com.exceptions.InvalidEmailException;
import org.com.exceptions.InvalidPasswordException;
import org.com.exceptions.InvalidPhoneNumberException;
import org.com.repos.OrderRepo;
import org.com.repos.ProductRepo;
import org.com.repos.UserRepo;
import org.com.service.CartService;
import org.com.service.OrderService;
import org.com.service.ProductService;
import org.com.service.UserService;
import org.com.tools.ProductListParser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Примеры возможностей программы:
 *
 * Вывод доступных для покупки товаров
 * Фильтрация товаров по ключевым словам, ценам, производителям
 * Составление продуктовой корзины пользователя
 * Трекинг заказа в системе доставки
 * Возврат заказа, повтор заказа
 */
public class Main { // StartShop
    public static void main(String[] args) throws Exception {
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

        File userRegistrationFile = new File("userInputForRegistration.txt"); // file with user registration info
        Scanner userInput = new Scanner(System.in);
        //1. Вход
        //2. Регистрация
        //0. Выход

        User currentUser = null;
        while (currentUser == null) {
            System.out.print("""
                    1. Вход
                    2. Регистрация
                    0. Выход
                    """);
            int select = userInput.nextInt();
            userInput.nextLine();
            switch (select) {

// TODO LOGIN
                case 1:
                    System.out.println("Login");
                    while (true) {
                        System.out.print("""
                                Type login(user email):
                                """);
                        String email = userInput.nextLine();
                        System.out.print("""
                                Type password:
                                """);
                        String password = userInput.nextLine();
                        if (userService.getUserByEmail(email).isPresent()) {
                            userService.login(email, password);
                            currentUser = userService.getUserByEmail(email).get();
                            System.out.println("User exist: " + currentUser.getUserId());
                            System.out.println("User logging. \nHello " + currentUser.getUserName());
                        } else {
                            System.out.print("""
                                    no user
                                    something typed wrong OR must register
                                    """);
                        }
                        break;
                    }
                    break;
// REGISTRATION
                case 2:
                    System.out.println("Registration");
                    while (true) {
                        Scanner input = new Scanner(userRegistrationFile);
// NAME
                        System.out.println("Type name: ");
                        String userName = input.nextLine();
// PH NUM
                        System.out.println("Type phone number: ");
                        String userPhNum = "";
                        while (true) {
                            try {
                                String userPhNumCheck = input.nextLine();
                                if (!validateNumber(userPhNumCheck)) {
                                    continue;
                                }
                                userPhNum = userPhNumCheck;
                                break;
                            } catch (InvalidPhoneNumberException e) {
                                System.out.println(e.getMessage());
                            }
                        }
// EMAIL
                        System.out.println("Type email: ");
                        String userEmail = "";
                        while (true) {
                            try {
                                String userEmailCheck = input.nextLine();
                                if (!EmailValidator.getInstance().isValid(userEmailCheck)) {
                                    System.out.print("""
                                            ?EmailIncorrect?
                                                Valid email address:
                                                email@example.com
                                                firstname.lastname@example.com
                                                firstname+lastname@example.com
                                                email@subdomain.example.com
                                                1234567890@example.com
                                            """);
                                    continue;
                                }
                                userEmail = userEmailCheck;
                                break;
                            } catch (InvalidEmailException e) {
                                System.out.println(e.getMessage());
                            }
                        }
// PASSWORD
                        System.out.println("Type password: ");
                        String userPassword = "";
                        while (true) {
                            try {
                                String userPasswordCheck = input.nextLine();
                                if (!validatePassword(userPasswordCheck)) {
                                    continue;
                                }
                                userPassword = userPasswordCheck;
                                break;
                            } catch (InvalidPasswordException e) {
                                System.out.println(e.getMessage());
                            }
                        }
// ?UserExist?
                        if (userService.getUserByEmail(userEmail).isEmpty() && userService.getUserByPhoneNumber(userPhNum).isEmpty()) {
                            userService.registerUser(userName, userPhNum, userEmail, userPassword);
                            currentUser = userService.getUserByEmail(userEmail).get();
                            System.out.println("User logging. \nHello " + currentUser.getUserName());
                        } else {
                            System.out.print("""
                                    no user
                                    """);
                        }
                        break;
                    }
                    break;

                case 0:
                    System.out.println("BYE");
                    break;
                default:
                    System.out.println("?NoCommand?");
                    break;
            }
            if (select == 0) {
                break;
            }
/** // парсер списка продуктов
            try {
                ProductListParser.jsonParser();
            } catch (IOException e) {
                System.out.println("Error reading file. File corrupted or no exist: "+e.getMessage());
            }
*/

// вывод списка продуктов
            System.out.print("""
                    Available Products List:
                    """);
            List<Product> productList = productService.getAllProduct();
            productList.forEach(System.out::println);
//            for (Product p : productList) {

//                System.out.println(p);

//                System.out.printf("""
//                                ID %s [%s] | %s, Brand: %s - %s
//                                """,                            // %.2f%n for price
//                        p.getProductId(),
//                        p.getProductCategory(),
//                        p.getProductName(),
//                        p.getProductBrand(),
//                        p.getProductPrice());
//            }
        }

        while (true){

        }

/** old test main
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

        // get order by user
        List<Order> orderByUser = orderService.getOrderByUser(userRepo.findByEmail("levelvogor@hotmail.com").get());
        for (Order userOrder : orderByUser) {
            System.out.printf("""
                            
                            User order ID: %s
                            User: %s | %s (%s | %s)
                            Order status: %s
                            Order total price: %s
                            Order address: %s
                            Order list:
                            """, userOrder.getOrderID(),
                    userOrder.getUser().getUserId(), userOrder.getUser().getUserName(), userOrder.getUser().getUserEmail(), userOrder.getUser().getUserPhoneNumber(),
                    userOrder.getOrderCurrentStatus(),
                    userOrder.getOrderTotalPrice(),
                    userOrder.getOrderDeliveryAddress()
            );
        }

        // user order list
        for (CartItem item : order.getOrderProductList()) {
            System.out.printf("""
                    %s | %s QTY * %s pc/pc -> for %s
                    """, item.getProduct().getProductName(),
                    item.getQuantity(),
                    item.getProduct().getProductPrice(),
                    item.getProduct().getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        if (cartService.getItem().isEmpty()){
            System.out.println("Cart is empty");
            System.out.println("Ready for new order");
        }
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