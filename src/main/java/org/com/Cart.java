package org.com;

import org.com.service.CartService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// CartServiceImpl or Cart — это временное состояние:
// пользователь добавляет/удаляет товары,
// меняет количество,
// корзина динамически рассчитывает общую сумму через getTotalPrice().
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

    // Метод ожидает конкретный объект Product и количество quantity.
    public void addItem(Product product, int quantity) {
        for (CartItem item : productsInCart) {
            if (item.getProduct().equals(product)) {
                //Если товар найден, мы обновляем его количество и сразу выходим из метода.
//                if (quantity < 0) {
                    item.setQuantity(item.getQuantity() + quantity);
                    System.out.printf("""
                                    ?Item's %s quantity changed to %s?
                                    """
                            , product.getProductName(), quantity);
//                }
                return;
            }
        }
        //Если цикл прошёл по всей корзине и ничего не нашёл, создаётся и добавляется новая позиция CartItem.
        CartItem cartItem = new CartItem(product, quantity);
        productsInCart.add(cartItem);
        if (quantity <= 0) {
            System.out.printf("""
                            ?Item %s in quantity of %s added to cart?
                            """
                    , product.getProductName(), quantity);
        }
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
        if (quantity == 0) {
            removeItem(product);
            System.out.printf("""
                            ?Item %s removed from cart?
                            """
                    , product.getProductName());
            return;
        }
        // Если есть такой product, то setQuantity
        for (CartItem item : productsInCart) {
            if (item.getProduct().equals(product)) {
                item.setQuantity(quantity);
                System.out.printf("""
                                ?Item's %s quantity changed to %s?
                                """
                        , product.getProductName(), item.getQuantity());
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

//        List<CartItem> unmodList = Collections.unmodifiableList(productsInCart);
//        String cartStr = unmodList.toString();
//        System.out.printf("""
//                Cart items:
//                %s
//                """,cartStr);
        return Collections.unmodifiableList(productsInCart); //  creates an unmodifiable view. It is not immutable because it changes if you're changing the original, backing collection
    }

    @Override
    public String toString() {
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

    @Override
    public void clearCart() {
/** ConcurrentModificationException here
        for (CartItem item : productsInCart) {
            productsInCart.remove(item);
        }
*/
        productsInCart.clear();
        System.out.println("?CartEmpty?");
    }
}