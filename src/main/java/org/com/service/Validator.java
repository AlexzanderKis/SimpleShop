package org.com.service;

import org.com.Order;
import org.com.Product;

public interface Validator {
    void validateProduct(Product product);

    void validateOrder(Order order);
}
