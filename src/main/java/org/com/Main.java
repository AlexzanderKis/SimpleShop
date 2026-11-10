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
import org.com.service.impl.Cart;
import org.com.service.impl.OrderServiceImpl;
import org.com.service.impl.ProductServiceImpl;
import org.com.service.impl.UserServiceImpl;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Примеры возможностей программы:
 * ------------------------------
 * Вывод доступных для покупки товаров
 * Фильтрация товаров по ключевым словам, ценам, производителям
 * Составление продуктовой корзины пользователя
 * Отмена заказа, повтор заказа
 * Трекинг заказа в системе доставки
 */
public class Main { // StartShop
    public static void main(String[] args) throws Exception {
        System.out.print("""
                    <*Магазин Открыт*>
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
                    System.out.println("Логин");
                    while (true) {
                        System.out.print("""
                                Логин пользователя (email):
                                """);
                        String email = userInput.nextLine();
                        System.out.print("""
                                Пароль пользователя:
                                """);
                        String password = userInput.nextLine();
                        if (userService.getUserByEmail(email).isPresent()) {
                            userService.login(email, password);
                            currentUser = userService.getUserByEmail(email).get();
                            System.out.println("Пользователь существует: " + currentUser.getUserId());
                            System.out.println("Пользователь вошёл. \nПривет " + currentUser.getUserName());
                        } else {
                            System.out.print("""
                                    Нет пользователя
                                    Что-то введено неверно или пользователь не существует
                                    """);
                        }
                        break;
                    }
                    break;
// REGISTRATION
                case 2:
                    System.out.println("Регистрация");
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

// Вывод списка продуктов доступных к покупке
            System.out.print("""
                    Список продуктов доступных к покупке:
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
// меню каталога, фильтрации, оформление заказа
        while (true) {
            System.out.print("""
                    1. Составить продуктовую корзину (Начать покупки).
                    2. Список заказов.
                    0. Выход.
                    """);
            int select = userInput.nextInt();
            userInput.nextLine();
// Main switch
            switch (select) {
                case 1:
                    System.out.print("""
                                * Добавление продукта в корзину: 'ID продукта' | 'количество'
                                * Изменение количества продукта: 'ID продукта' | 'количество'
                                * Удаление из корзины: 'ID продукта' | 'количество = 0'
                            1. Вывести список товаров в корзине.
                            2. Фильтрация товаров по ключевым словам.
                            3. Фильтрация по ценам.
                            4. Список доступных к покупке товаров.
                            0. Закончить составление корзины.
                            """);

                    while (true) {
                        long prodID = userInput.nextLong();

// Закончить составление корзины
                        if (prodID == 0) {
                            break;
                        }

// Вывести список товаров в корзине
                        if (prodID == 1 && !cartService.getItem().isEmpty()) {
                            System.out.printf("""
                                    Ваша корзина:
                                    %sИтоговая цена: %s
                                    """, cartService, cartService.getTotalPrice());
                            System.out.print("""
                                    
                                        * Для составления/изменения корзины продолжайте вводить: 'ID продукта' | 'количество'
                                    2. Фильтрация товаров по ключевым словам.
                                    3. Фильтрация по ценам.
                                    4. Список доступных к покупке товаров.
                                    5. Оформить заказ
                                    6. Очистить корзину
                                    0. Back
                                    """);
                            continue;
                        } else if (prodID == 1 && cartService.getItem().isEmpty()) {
                            System.out.println("Корзина пуста");
                            continue;
                        } else if (prodID == 6) {
                            cartService.clearCart();
                            System.out.println("Корзина очищена");
                            continue;
                        }

// Фильтрация товаров по ключевым словам
                        if (prodID == 2) {
                            System.out.println("Введите ключевое слово (e.g. 'напиток', 'яблоко':");
                            userInput.nextLine();
                            String userKeyword = userInput.nextLine();
                            System.out.printf("""
                                    Продукты по слову: %s
                                    """, userKeyword);
                            List<Product> productList = productService.searchProductByKeyword(userKeyword);
                            if (productList.isEmpty()) {
                                System.out.println("none");
                            } else {
                                productList.forEach(System.out::println);
                            }
//                            productService.searchProductByKeyword(userKeyword).forEach(System.out::println);
                            System.out.print("""
                                    
                                        * Добавление продукта в корзину: 'ID продукта' | 'количество'
                                        * Изменение количества продукта: 'ID продукта' | 'количество'
                                        * Удаление из корзины: 'ID продукта' | 'количество = 0'
                                    1. Вывести список товаров в корзине.
                                    2. Фильтрация товаров по ключевым словам.
                                    3. Фильтрация по ценам.
                                    4. Список доступных к покупке товаров.
                                    0. Закончить составление корзины.
                                    """);
                            continue;
                        }

// Фильтрация товаров по ценам
                        if (prodID == 3) {
                            System.out.println("Диапазон цен доступных продуктов от 99.99 до 9999.99");
                            System.out.println("Напишите мин и мах цену: (e.g. 123 4567)");
                            int num1 = userInput.nextInt();
                            int num2 = userInput.nextInt();
                            int min = Math.min(num1, num2);
                            int max = Math.max(num1, num2);
                            if (min > 0 && max > min) {
                                System.out.printf("""
                                        Фильтрация по цене от %d до %d:
                                        """, min, max);
                                List<Product> productList = productService
                                        .filterProductByPriceRange(BigDecimal.valueOf(min), BigDecimal.valueOf(max));
                                productList.forEach(System.out::println);
                                System.out.print("""
                                        
                                            * Добавление продукта в корзину: 'ID продукта' | 'количество'
                                            * Изменение количества продукта: 'ID продукта' | 'количество'
                                            * Удаление из корзины: 'ID продукта' | 'количество = 0'
                                        1. Вывести список товаров в корзине.
                                        2. Фильтрация товаров по ключевым словам.
                                        3. Фильтрация по ценам.
                                        4. Список доступных к покупке товаров.
                                        0. Закончить составление корзины.
                                        """);
                            } else {
                                System.out.println("Цена не может быть отрицательной");
                                System.out.print("""
                                        
                                            * Добавление продукта в корзину: 'ID продукта' | 'количество'
                                            * Изменение количества продукта: 'ID продукта' | 'количество'
                                            * Удаление из корзины: 'ID продукта' | 'количество = 0'
                                        1. Вывести список товаров в корзине.
                                        2. Фильтрация товаров по ключевым словам.
                                        3. Фильтрация по ценам.
                                        4. Список доступных к покупке товаров.
                                        0. Закончить составление корзины.
                                        """);
                            }
                            continue;
                        }

// Список продуктов доступных к покупке
                        if (prodID == 4) {
                            System.out.print("""
                                    Список продуктов доступных к покупке:
                                    """);
                            List<Product> productList = productService.getAllProduct();
                            productList.forEach(System.out::println);
                            System.out.println();
                            continue;
                        }

// Оформление заказа
                        if (prodID == 5 && !cartService.getItem().isEmpty()) {
                            userInput.nextLine(); // сбросить строку
                            orderCreationStation(currentUser, cartService, orderService, userInput);
/*
                            userInput.nextLine(); // сбросить строку
                            System.out.println("Order Creation Station");
                            System.out.println("Type address:");
                            String deliveryAddress = userInput.nextLine();
                            if (deliveryAddress.isEmpty()) {
                                System.out.println("No address - no order.");
                                continue;
                            } else {
                                orderService.createOrder(currentUser, deliveryAddress, cartService);
                            }
 */
                            break;
                        }

// Если ID товара не существует
                        if (productService.getProductById(prodID).isEmpty()) {
                            System.out.printf("""
                                    ID продукта %d не существует
                                    """, prodID);
                            continue;
                        }

                        int qty = userInput.nextInt(); // переменная для количества товара

// Заглядываем в продукты и ищем товар
                        Optional<Product> productOptional = Optional.ofNullable(productRepo.findById(prodID));
                        Product product = productOptional.orElse(null);
                        boolean prodExistInCart = cartService.getItem()
                                .stream()
                                .anyMatch(cartItem -> cartItem.getProduct().equals(product));

// Если товар есть в корзине и кол-во > 0, то обновляем количество++
                        if (prodExistInCart && qty > 0) {
                            cartService.updateQuantity(productService.getProductById(prodID).orElse(null), qty);
                            System.out.printf("""
                                    Количество продукта %s увеличено на %s шт.
                                    """, productRepo.findById(prodID).getProductName(), qty);

// Если товар есть в корзине и кол-во < 0 обновляем количество--
                        } else if (prodExistInCart && qty < 0) {
                            cartService.updateQuantity(productService.getProductById(prodID).orElse(null), qty);
                            System.out.printf("""
                                    Количество продукта %s уменьшено на %s шт.
                                    """, productRepo.findById(prodID).getProductName(), Math.abs(qty));

// Удаление товара из корзины если кол-во == 0
                        } else if (qty == 0) {
                            cartService.removeItem(productService.getProductById(prodID).orElse(null));
                            System.out.printf("""
                                    Продукт %s удален из корзины
                                    """, productRepo.findById(prodID).getProductName());

// Если товара нет в корзине - добавляем товар и кол-во
                        } else {
                            cartService.addItem(productRepo.findById(prodID), qty);
                            System.out.printf("""
                                    Продукт %s в %s количестве добавлен корзину
                                    """, productRepo.findById(prodID).getProductName(), qty);
                            System.out.print("""
                                        * Для составления/изменения корзины продолжайте вводить: 'ID продукта' | 'количество'
                                        * 1. Посмотреть корзину.
                                        * 5. Оформить заказ.
                                    """);
                        }
                    }
                    break;
/** old cases
 // Оформление повторного заказа
 case 2:
 // TODO - сохранение/исправление адреса доставки

 orderService.repeatOrder(orderRepo.findByUser(currentUser).getLast(), cartService);
 System.out.printf("""
 Вы повторили заказ:
 %s
 """, cartService);
 System.out.print("""
 Оформить заказ? Y/N
 """);
 //                    userInput.nextLine(); // сбросить строку
 String in = userInput.nextLine();
 if (in.equalsIgnoreCase("y")) {
 orderCreationStation(currentUser, cartService, orderService, userInput);
 System.out.println("Заказ оформлен");
 } else {
 System.out.print("""
 Заказ сохранён, как "неоформленный"
 """);
 continue;
 }
 break;

 // Отменить заказ
 case 3:
 List<Order> orderList = orderService.getOrderByUser(currentUser);
 printOrderList(orderList);
 if (orderList.isEmpty()) {
 continue;
 }
 System.out.println("Введите номер заказа для отмены (Enter для возврата):");
 String userIN = userInput.nextLine();
 try {
 int index = Integer.parseInt(userIN) - 1;
 orderService.cancelOrder(orderList.get(index));
 System.out.printf("""
 Заказ #%s отменён.
 """, userIN);
 } catch (IllegalStateException e) {
 System.out.println(e.getMessage());
 } catch (IndexOutOfBoundsException e) {
 System.out.println("Заказа с таким номером нет.");
 } catch (NumberFormatException e) {
 // пустой ввод – просто возврат
 }
 break;
 */

// Список заказов пользователя по запросу
                case 2:
                    while (true) {
                        List<Order> userOrders = orderService.getOrderByUser(currentUser);
                        printOrderList(userOrders);

                        if (userOrders.isEmpty()) {
                            break;
                        }
                        System.out.print("""
                                Выберите номер заказа для дальнейших действий. (ENTER для возврата):
                                """);

                        String inLine = userInput.nextLine().trim();
                        if (inLine.isEmpty()) {
                            break;
                        }

                        try {
                            int index = Integer.parseInt(inLine) - 1;
                            if (index < 0 || index >= userOrders.size()) {
                                System.out.println("Неверный номер заказа. Попробуйте снова. (ENTER для возврата):");
                                continue;
                            }
                            Order selectedOrder = userOrders.get(index);
                            // Показать меню действий с выбранным заказом
                            orderActionMenu(selectedOrder, orderService, cartService, userInput);
                            // После завершения действия цикл продолжается – снова показываем список
                            userOrders = orderService.getOrderByUser(currentUser);
                            if (userOrders.isEmpty()) {
                                System.out.println("Нет заказов.");
                                break; // выходим из case 2 в главное меню
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Введите число или нажмите Enter для выхода.");
                        }
                    }
                    break;

// Выход из программы
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
    }

    // Метод выводит доступные действия для конкретного заказа и выполняет их
    private static void orderActionMenu(Order order,
                                        OrderService orderService,
                                        CartService cartService,
                                        Scanner userInput) {
        boolean inOrderActionMenu = true;
        while (inOrderActionMenu) {
            System.out.printf("""
                    Выбран заказ:
                    %s
                    """, order);
            System.out.print("""
                    1. Оформить заказ.
                    2. Отменить заказ.
                    3. Повторить заказ.
                    4. Изменить адрес доставки.
                    0. Назад к списку заказов.
                    """);

            String ch = userInput.nextLine();
            int select;
            try {
                select = Integer.parseInt(ch);
            } catch (NumberFormatException e) {
                System.out.println("Введите число");
                continue;
            }
// Sub-main switch
            switch (select) {

// Оформление уже созданного заказа
                case 1:
                    System.out.print("""
                            //                            Order confirmation station
                            """);
                    if (order.getOrderCurrentStatus() != OrderStatus.CREATED) {
                        System.out.print("""
                                Заказ нельзя оформить
                                """);
                        System.out.printf("""
                                Статус заказа: %s
                                
                                """, order.getOrderCurrentStatus());
                    } else {
                        System.out.print("Введите адрес доставки: ");
                        String address = userInput.nextLine().trim();
                        if (address.isEmpty()) {
                            System.out.println("Адрес не может быть пустым.");
                            break;
                        }
                        Order confirmedOrder = new Order(
                                order.getUser(),
                                order.getOrderID(),
                                order.getOrderProductList(),
                                order.getOrderTotalPrice(),
                                order.getOrderTotalPriceWithDiscount(),
                                OrderStatus.NEW_ORDER,
                                address,
                                LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy dd MM | hh:mm:ss a")))); // дата меняется на новую при оформлении
                        orderService.updateOrder(confirmedOrder);
                        System.out.println("Заказ оформлен!");
                        inOrderActionMenu = false;
                        break;
                    }
                    break;

// Отмена заказа
                case 2:
                    System.out.print("""
                            //                            Order cancellation station
                            """);
                    try {
                        orderService.cancelOrder(order);
                        System.out.printf("""
                                Заказ #%s отменён.
                                
                                """, order.getOrderID());
                        inOrderActionMenu = false; // возврат в меню
                    } catch (IllegalStateException e) {
                        System.out.println(e.getMessage());
                        inOrderActionMenu = false;
                    } catch (IndexOutOfBoundsException e) {
                        System.out.print("""
                                Заказа с таким номером нет.
                                
                                """);
                        inOrderActionMenu = false;
                    } catch (NumberFormatException e) {
                        // пустой ввод – просто возврат
                        inOrderActionMenu = false;
                    }
                    break;

// Повтор существующего заказа
                case 3:
// TODO - сохранение/исправление адреса доставки

                    System.out.print("""
                            //                            Order repetition station
                            """);
                    Order newOrder = orderService.repeatOrder(order, cartService);
                    System.out.printf("""
                            Повтор заказа:
                            %s
                            Товары из заказа добавлены в корзину.
                            """, cartService);
//                    System.out.println("Товары из заказа добавлены в корзину.");
                    System.out.print("""
                            Оформить заказ? Y/N
                            """);
                    String userAnswer = userInput.nextLine().trim();
                    if (userAnswer.equalsIgnoreCase("Y")) {
                        // Оформляем новый заказ – запрашиваем адрес и меняем статус
                        System.out.print("Введите адрес доставки: ");
                        String address = userInput.nextLine().trim();
                        if (address.isEmpty()) {
                            System.out.println("Адрес не может быть пустым. Заказ останется в статусе CREATED.");
                        } else {
                            Order updatedOrder = new Order(
                                    newOrder.getUser(),
                                    newOrder.getOrderID(),
                                    newOrder.getOrderProductList(),
                                    newOrder.getOrderTotalPrice(),
                                    newOrder.getOrderTotalPriceWithDiscount(),
                                    OrderStatus.NEW_ORDER,
                                    address,
                                    LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy dd MM | hh:mm:ss a")))); // дата меняется
                            orderService.updateOrder(updatedOrder);
                            System.out.print("""
                                    Заказ оформлен (статус сменён NEW_ORDER).
                                    
                                    """);
                            inOrderActionMenu = false;
                            break;
                        }
                    } else {
                        System.out.print("""
                                Заказ сохранён со статусом CREATED. Можно оформить заказ позже.
                                
                                """);
                    }
                    // Выход из подменю действий
                    inOrderActionMenu = false;
                    break;

// Изменить адрес доставки в заказе
                case 4:
                    System.out.print("""
                            //                            Order address correction station
                            """);
                    System.out.print("""
                            Введите новый адрес доставки:
                            """);
                    String newAddress = userInput.nextLine().trim();
                    if (newAddress.isEmpty()) {
                        System.out.print("""
                                Адрес не может быть пустым.
                                """);
                        continue;
                    }
                    Order updatedOrder = new Order(order.getUser(),
                            order.getOrderID(),
                            order.getOrderProductList(),
                            order.getOrderTotalPrice(),
                            order.getOrderTotalPriceWithDiscount(),
                            order.getOrderCurrentStatus(),
                            newAddress,
                            order.getOrderCreationDate());

                    orderService.updateOrder(updatedOrder);
                    order = updatedOrder;
                    break;

                case 0:
                    // Назад к списку заказов
                    System.out.println("Возврат в подменю");
                    return;

                default:
                    System.out.println("?NoCommand?");
                    break;
            }
        }
    }

    // Метод оформления заказа
    private static void orderCreationStation(User currentUser,
                                             CartService cartService,
                                             OrderService orderService,
                                             Scanner userInput) {
// проверка авторизации
        if (currentUser == null) {
            System.out.println("Нет пользователя");
            return;
        }
// проверка корзины
        if (cartService.getItem().isEmpty()) {
            System.out.println("Корзина пуста");
            return;
        }

        System.out.print("""
                //                <Order Creation Station>
                """);
        System.out.print("""
                Введите адрес доставки:
                """);
//        userInput.nextLine(); // сброс буфера
        String deliveryAddress = userInput.nextLine().trim();
        if (deliveryAddress.isEmpty()) {
            System.out.println("Нет адреса - нет заказа");
            return;
        }
        orderService.createOrder(currentUser, deliveryAddress, cartService);
        System.out.println("DONE");
    }

    // Вывод списка заказов
    private static void printOrderList(List<Order> orderList) {
        if (orderList.isEmpty()) {
            System.out.println("Нет заказов.");
        } else {
            System.out.println("<<<Ваши заказы>>>");
            for (int i = 0; i < orderList.size(); i++) {
                Order order = orderList.get(i);
                System.out.printf("""
                        Заказ №%d
                        """, i + 1);
                System.out.println(order);
                System.out.println("-------------------------");
            }
        }
    }
}