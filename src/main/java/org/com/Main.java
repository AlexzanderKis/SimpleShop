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
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
                        Scanner input = new Scanner(userRegistrationFile); // for file usage (remove for user input usage)
// NAME
                        System.out.println("Type name: ");
                        String userName = input.nextLine(); // for file usage (remove for user input usage)
//                        String userName = userInput.nextLine(); // for user input
// PH NUM
                        System.out.println("Type phone number: ");
                        String userPhNum = "";
                        while (true) {
                            try {
                                String userPhNumCheck = input.nextLine(); // for file
//                                String userPhNumCheck = userInput.nextLine(); // for user input
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
                                String userEmailCheck = input.nextLine(); // for file
//                                String userEmailCheck = userInput.nextLine(); // for user input
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
                                String userPasswordCheck = input.nextLine(); // for file
//                                String userPasswordCheck = userInput.nextLine(); // for user input
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
            System.out.println();
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
// меню каталога и фильтрации
        while (true) {
            System.out.print("""
                    1. Составить продуктовую корзину (Начать покупки)
                    2. Фильтрация товаров по ключевым словам
                    3. Фильтрация по ценам
                    0. Выход
                    """);
            int select = userInput.nextInt();
            userInput.nextLine();
            switch (select) {
                case 1:
                    System.out.print("""
                            Добавление продукта в корзину: 'ID продукта' | 'количество'
                            Изменение количества продукта: 'ID продукта' | 'количество'
                            Удаление из корзины: 'ID продукта' | 'количество = 0'
                            Вывести список товаров в корзине: 1
                            Закончить составление корзины: 0
                            """);
                    while (true) {
                        long prodID = userInput.nextLong();

                        // Закончить составление корзины
                        if (prodID == 0) {
                            break;
                        }

                        // Вывести список товаров в корзине
                        if (prodID == 1) {
                            System.out.print(cartService);
                            System.out.print("""
                                    
                                    Если готовы оформить заказ нажмите: Y/y
                                    Для составления корзины продолжайте вводить: 'ID продукта' | 'количество'
                                    Закончить составление корзины: 0
                                    """);
                            continue;
                        }

                        int qty = userInput.nextInt();

                        // Заглядываем в корзину и ищем товар
                        Optional<Product> productOptional = Optional.ofNullable(productRepo.findById(prodID));
                        Product product = productOptional.orElse(null);
                        boolean prodExistInCart = cartService.getItem()
                                .stream()
                                .anyMatch(cartItem -> cartItem.getProduct().equals(product));

                        // Если ID товара не существует
                        if (productService.getProductById(prodID).isEmpty()){
                            System.out.printf("""
                                    Item ID %d not exist
                                    """, prodID);
                            continue;
                        }

                        // Если товар есть в корзине и кол-во > 0, то обновляем количество++
                        if (prodExistInCart && qty > 0) {
                            cartService.updateQuantity(productService.getProductById(prodID).orElse(null), qty);
                            System.out.printf("""
                                    Item's %s quantity increased by %s QTY
                                    """, productRepo.findById(prodID).getProductName(), qty);

                            // Если товар есть в корзине и кол-во < 0 обновляем количество--
                        } else if (prodExistInCart && qty < 0) {
                            cartService.updateQuantity(productService.getProductById(prodID).orElse(null), qty);
                            System.out.printf("""
                                    Item's %s quantity decreased by %s
                                    """, productRepo.findById(prodID).getProductName(), qty);

                            // Удаление товара из корзины если кол-во == 0
                        } else if (qty == 0) {
                            cartService.removeItem(productService.getProductById(prodID).orElse(null));
                            System.out.printf("""
                                    Item %s removed from cart
                                    """, productRepo.findById(prodID).getProductName());

                            // Если товара нет в корзине - добавляем товар и кол-во
                        } else {
                            cartService.addItem(productRepo.findById(prodID), qty);
                            System.out.printf("""
                                    Item %s in %s QTY added to cart
                                    """, productRepo.findById(prodID).getProductName(), qty);
                        }
                    }
                    break;

                case 2:
                    // TODO -> Фильтрация товаров по ключевым словам
                    break;

                case 3:
                    System.out.println("Type min and max price: (e.g. 123 4567)");
                    int min = userInput.nextInt(), max = userInput.nextInt();
                    System.out.printf("""
                            Filtered by price from %d to %d:
                            """, min,max);
                    List<Product> productList = productService.filterProductByPriceRange(BigDecimal.valueOf(min), BigDecimal.valueOf(max));
                    productList.forEach(System.out::println);
                    System.out.println();
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