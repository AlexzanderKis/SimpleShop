package org.com.service.impl;

import org.com.domain.dto.CartDTO;
import org.com.domain.dto.CartItemDTO;
import org.com.domain.entity.ProductEntity;
import org.com.exceptions.ResourceNotFoundException;
import org.com.repos.ProductRepo;
import org.com.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// CartServiceImpl or Cart — это временное состояние:
// пользователь добавляет/удаляет товары, меняет количество,
// корзина динамически рассчитывает общую сумму через getTotalPrice().

/**
       Реализация сервиса корзины
       Корзина хранится в памяти, привязана к HTTP-сессии (@SessionScope) -
       у каждого пользователя своя корзина, которая исчезает после закрытия сессии/перезапуска сервера

       Все методы synchronized, потому что в рамках одной сессии возможны параллельные запросы (например, несколько вкладок)
       Список items - ArrayList, не потокобезопасен сам по себе
  TODO
       В реальном проекте корзину лучше хранить в БД или Redis, чтобы она переживала перезапуск
 */

/** TODO
        Корзина не хранится в БД.
        Она привязана к сессии пользователя
        +: быстро, не нужно создавать таблицу cart.
        -: при перезапуске сервера корзины теряются;
        при горизонтальном масштабировании (несколько серверов) сессии нужно синхронизировать

        Альтернативы в реальных проектах:
        Хранить корзину в БД (cart и cart_items)
        Хранить в Redis

 * @SessionScope только для временной корзины гостя, а для авторизованных - БД.
 */
@Service
@SessionScope
public class CartServiceImpl implements CartService {

    private final ProductRepo productRepo;
    private final List<CartItemDTO> items = new ArrayList<>();

    public CartServiceImpl(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    /**
     @SessionScope создаёт один экземпляр на сессию,
     но в рамках одной сессии может быть несколько параллельных запросов (например, пользователь открыл две вкладки и одновременно добавил товары)
     synchronized защищает items от состояния гонки

     TODO -> в идеале в будущем использовать ReentrantLock или ConcurrentHashMap
     */

    /**
             addItem() Добавляет товар в корзину
             Если товар уже есть - увеличивает количество
             Если нет - загружает из БД и создаёт новую позицию со снимком текущей цены/названия

             @param productId ID товара
             @param quantity количество (должно быть > 0)
             @return обновлённое состояние корзины
             @throws IllegalArgumentException если quantity <= 0
             @throws ResourceNotFoundException если товар с таким ID не найден в каталоге
     */
    // Метод ожидает конкретный объект Product и количество quantity.
    @Override
    public synchronized CartDTO addItem(Long productId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество добавляемого товара должно быть положительным числом");
        }

        // Ищем товар в корзине
        for (CartItemDTO item : items) {
            // Если товар найден, мы обновляем его количество. Mini updateQuantity()
            if (item.getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                return getCart();
            }
        }

        // Идём в БД если в корзине еще нет
        ProductEntity product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Товар с ID " + productId + " не найден в каталоге"));

        // Если цикл прошёл по всей корзине и ничего не нашёл, создаётся и добавляется новая позиция CartItemDto
        CartItemDTO newItem = new CartItemDTO(
                product.getId(),
                product.getName(),
                product.getCategory().name(),
                product.getBrand(),
                product.getPrice(),
                quantity,
                product.getImageUrl());

        /** TODO Потенциальная проблема:
                 если товар удалён из каталога,
                 но лежит в корзине, findById вернёт пустой Optional,
                 и пользователь получит ResourceNotFoundException.

                 Логично было бы при загрузке корзины помечать такие позиции как "недоступны". Это продакшн-требования.
         */

        items.add(newItem);
        return getCart();
    }

    /**
            Изменяет количество товара в корзине на величину newQuantity (прибавляет newQuantity к текущему quantity).
            Если итоговое количество <= 0, позиция удаляется
            Если товара нет в корзине и Quantity >0 -> добавляет как новый товар

            ВАЖНО: параметр - это ДЕЛЬТА, а не новое абсолютное значение
            @param productId ID товара
            @param newQuantity изменение количества (например, +1, -1, +5)
            @return обновлённое состояние корзины
     */
    @Override
    public synchronized CartDTO updateQuantity(Long productId, int newQuantity) {
        for (int i = 0; i < items.size(); i++) {
            CartItemDTO item = items.get(i);
            if (item.getProductId().equals(productId)) {
                int newQty = item.getQuantity() + newQuantity;
                // Если количество product <= 0, то removeItem() из корзины, если >0 добавить количество setQuantity()
                if (newQty <= 0) {
                    items.remove(i);
                } else {
                    item.setQuantity(newQty);
                }
                return getCart();
            }
        }

        if (newQuantity > 0) {
            return addItem(productId, newQuantity);
        }

        return getCart();
    }

    /**
         @method getCart()
         @method getItems()
         оба метода возвращают копию списка, а не сам items
         Это защита, чтобы вызывающий код случайно не изменил внутреннее состояние корзины
         unmodifiableList дополнительно запрещает модификацию через возвращённый список

        TODO Подводные:
             CartDto копирует ссылки на CartItemDto, а не сами объекты
             Если кто-то изменит CartItemDto через getCart().getItems().get(0).setQuantity(...), это отразится на внутреннем состоянии
     */
    @Override
    public synchronized CartDTO getCart() {
        return new CartDTO(new ArrayList<>(items));
    }

    @Override
    public List<CartItemDTO> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items)); //  creates an unmodifiable view. It is not immutable because it changes if you're changing the original, backing collection
    }

    // Возвращает стоимость корзины с товарами
    @Override
    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        BigDecimal totalCost;
//        BigDecimal totalPrice = BigDecimal.ZERO;

        // На каждом шаге цикла рассчитывается стоимость текущей позиции (totalCost) и прибавляется к общей сумме (totalPrice).
//        for (CartItemDTO item : productsInCart) {
//            BigDecimal price = item.getProduct().getProductPrice();
//            int quantity = item.getQuantity();
//            totalCost = price.multiply(BigDecimal.valueOf(quantity));
//            totalPrice = totalPrice.add(totalCost);
//        }
//        return totalPrice;
    }


    // Удаляет из корзины товар
    @Override
    public synchronized CartDTO removeItem(Long productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        return getCart();
    }

    // очистка корзины
    @Override
    public synchronized void clearCart() {
/**
 ConcurrentModificationException here
 for (CartItem item : productsInCart) {
 productsInCart.remove(item);
 }
 */
        items.clear();
//        System.out.println("?CartEmpty?");
    }

/**
 @Override public String toString() {
 StringBuilder stringBuilder = new StringBuilder();
 for (CartItem item : productsInCart){
 if (item.getQuantity() == 0){
 continue;
 }
 stringBuilder.append(String.format("""
 ID %s [%s] | %s, Brand: %s - %s pc/pc * %s QTY = %s
 """,
 item.getProduct().getProductId(),
 item.getProduct().getProductCategory(),
 item.getProduct().getProductName(),
 item.getProduct().getProductBrand(),
 item.getProduct().getProductPrice(),
 item.getQuantity(),
 item.getProduct().getProductPrice().multiply(BigDecimal.valueOf(item.getQuantity()))));
 }
 return stringBuilder.toString();
 }
 */
}