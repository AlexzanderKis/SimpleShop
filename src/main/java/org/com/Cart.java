package org.com;

import org.com.service.CartService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart implements CartService {
//    CartItem cartItem = null;
    private final List<CartItem> productsInCart = new ArrayList<>();

/** BAD
 * Поле класса cartItem: Переменная CartItem cartItem объявлена на уровне класса, но не инициализирована (null).
 * Из-за этого вызов cartItem.setQuantity(...) приведет к ошибке NullPointerException.
 * Нам не нужно это поле на уровне всего класса Cart.
    @Override
    public void addItem(CartItem product, int quantity) {
        if (productsInCart.contains(product)){
            cartItem.setQuantity(product.getQuantity() + quantity);
        } else productsInCart.add(product);
    }
*/

    public void addItem(Product product, int quantity) {
        for (CartItem item : productsInCart) {
            if (item.getProduct().equals(product)) {
                //Если товар найден, мы обновляем его количество и сразу выходим из метода.
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        //Если цикл прошёл по всей корзине и ничего не нашёл, создаётся и добавляется новая позиция CartItem.
        CartItem cartItem = new CartItem(product, quantity);
        productsInCart.add(cartItem);
    }

    @Override
    public void removeItem(Product product) {
/** ConcurrentModificationException might be
        for (CartItem item : productsInCart) {
            if (item.getProduct().equals(product)) {
                productsInCart.remove(item);
                return;
            }
*/
        productsInCart.removeIf(cartItem -> cartItem.getProduct().equals(product));
    }

    @Override
    public void updateQuantity(Product product, int quantity) {
        // Если количество product = 0, то removeItem() из корзины
        if (quantity <= 0) {
            removeItem(product);
            return;
        }
        // Если есть такой product, то setQuantity
        for (CartItem item : productsInCart) {
            if (item.getProduct().equals(product)) {
                item.setQuantity(quantity);
                return;
            }
        }
    }

    @Override
    public BigDecimal getTotalPrice() {
        BigDecimal totalCost;
        BigDecimal totalPrice = BigDecimal.ZERO;

        // На каждом шаге цикла рассчитывается стоимость текущей позиции (totalCost) и прибавляется к общей сумме (totalPrice).
        for (CartItem item : productsInCart) {
            BigDecimal price = item.getProduct().getProductPrice();
            int quantity = item.getQuantity();
            totalCost = price.multiply(BigDecimal.valueOf(quantity));
            totalPrice = totalPrice.add(totalCost);
        }
        return totalPrice;
    }

    @Override
    public List<CartItem> getItem() {
//        return productsInCart;
//        return List.copyOf(productsInCart); // creates an immutable copy. Changing the original list will not affect it.
        return Collections.unmodifiableList(productsInCart); //  creates an unmodifiable view. It is not immutable because it changes if you're changing the original, backing collection
    }

    @Override
    public void clearCart() {
/** ConcurrentModificationException here
        for (CartItem item : productsInCart) {
            productsInCart.remove(item);
        }
*/
        productsInCart.clear();
    }
}