package org.com.service;

import org.com.CartItem;
import org.com.Product;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    void addItem(Product product, int quantity);

    void removeItem(Product product);

    void updateQuantity(Product product, int quantity);

    BigDecimal getTotalPrice();

    List<CartItem> getItem();

    void clearCart();
}